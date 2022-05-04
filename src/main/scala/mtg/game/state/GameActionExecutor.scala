package mtg.game.state

import mtg.abilities.{StaticAbility, TriggeredAbility}
import mtg.actions.moveZone.MoveToBattlefieldAction
import mtg.continuousEffects.PreventionEffect.Result.Prevent
import mtg.continuousEffects.{CharacteristicOrControlChangingContinuousEffect, FloatingActiveContinuousEffect, PreventionEffect, ReplacementEffect}
import mtg.core.{ObjectId, PlayerId}
import mtg.effects.EffectContext
import mtg.game.priority.PriorityChoice
import mtg.game.state.history.HistoryEvent
import mtg.instructions.nounPhrases.CardName
import mtg.instructions.verbs.EntersTheBattlefieldReplacementEffect

import scala.annotation.tailrec

object GameActionExecutor {

  sealed trait GameActionResult[+T]
  object GameActionResult {
    case class Value[T](value: T) extends GameActionResult[T]
    case class Action[T](action: GameAction[T]) extends GameActionResult[T]
    sealed trait Interrupted extends GameActionResult[Nothing]
    case class Backup(gameState: GameState) extends Interrupted
    case class GameOver(gameResult: GameResult) extends Interrupted
  }

  def handleDecision(gameState: GameState, serializedDecision: String, actingPlayer: PlayerId)(implicit stops: Stops): Option[GameState] = {
    runAction(gameState, handleDecisionForAction(_, serializedDecision, actingPlayer)(_))
      .map(executeAllActions)
  }

  def handleDecisionForAction[T](action: GameAction[T], serializedDecision: String, actingPlayer: PlayerId)(implicit gameState: GameState): Option[(GameActionResult[T], GameState)] = {
    action match {
      case directChoice: Choice[T] if directChoice.playerToAct == actingPlayer =>
        directChoice.handleDecision(serializedDecision).map(d => (GameActionResult.Value(d), gameState.recordChoice(directChoice, d)))
      case PartiallyExecutedActionWithChild(rootAction, child, initialGameState) =>
        handleDecisionForDelegate(rootAction, child, initialGameState, serializedDecision, actingPlayer)
      case PartiallyExecutedActionWithFlatMappedChild(rootAction, child, f, initialGameState) =>
        handleDecisionForDelegateWithFlatMap(rootAction, child, f, initialGameState, serializedDecision, actingPlayer)
      case _ =>
        None
    }
  }

  def handleDecisionForDelegate[T](
    rootAction: DelegatingGameAction[T],
    childAction: GameAction[T],
    initialGameState: GameState,
    serializedDecision: String,
    actingPlayer: PlayerId)(
    implicit gameState: GameState
  ): Option[(GameActionResult[T], GameState)] = {
    handleDelegatingActionWithChild[T](rootAction, childAction, initialGameState, handleDecisionForAction(_, serializedDecision, actingPlayer)(_))
  }

  def handleDecisionForDelegateWithFlatMap[T, S](
    rootAction: DelegatingGameAction[T],
    childAction: GameAction[S],
    f: S => GameAction[T],
    initialGameState: GameState,
    serializedDecision: String,
    actingPlayer: PlayerId)(
    implicit gameState: GameState
  ): Option[(GameActionResult[T], GameState)] = {
    handleDelegatingActionWithFlatMap[T, S](rootAction, childAction, f, initialGameState, handleDecisionForAction(_, serializedDecision, actingPlayer)(_))
  }

  private def runAction(gameState: GameState, f: (GameAction[RootGameAction], GameState) => Option[(GameActionResult[RootGameAction], GameState)]): Option[GameState] = {
    for {
      action <- gameState.currentAction
      (result, gameStateAfterAction) <- f(action, gameState)
    } yield updateGameState(gameStateAfterAction, result)
  }

  @tailrec
  def executeAllActions(gameState: GameState)(implicit stops: Stops): GameState = {
    executeNextAction(gameState) match {
      case Some(newGameState) =>
        executeAllActions(newGameState)
      case None =>
        gameState
    }
  }

  def executeNextAction(gameState: GameState)(implicit stops: Stops): Option[GameState] = {
    runAction(gameState, executeAction(_)(_, stops))
  }

  def executeAction[T](gameAction: GameAction[T])(implicit gameState: GameState, stops: Stops): Option[(GameActionResult[T], GameState)] = gameAction match {
    case priorityChoice: PriorityChoice if stops.shouldAutoPass(priorityChoice, gameState) =>
      handleDecisionForAction(priorityChoice, "Pass", priorityChoice.playerToAct)
    case _: Choice[T] =>
      None
    case action: DelegatingGameAction[T] =>
      Some(executeDelegatingAction(action))
    case PartiallyExecutedActionWithResult(rootAction, result, initialGameState) =>
      Some(recordExecutedEvent(rootAction, result, initialGameState, gameState))
    case PartiallyExecutedActionWithFlatMappedResult(rootAction, result, f: (Any => GameAction[T]), initialGameState) =>
      Some((unwrapChildAction(rootAction, f(result), initialGameState), gameState))
    case PartiallyExecutedActionWithChild(rootAction, childAction, initialGameState) =>
      executeDelegateWithChild(rootAction, childAction, initialGameState)
    case PartiallyExecutedActionWithFlatMappedChild(rootAction, childAction, f, initialGameState) =>
      executeDelegateWithFlatMap(rootAction, childAction, f, initialGameState)
    case gameObjectAction: GameObjectAction[T] =>
      Some(executeGameObjectAction(gameObjectAction))
    case LogEventAction(logEvent) =>
      Some((GameActionResult.Value(()).asInstanceOf[GameActionResult[T]], gameState.recordLogEvent(logEvent)))
  }

  private def executeDelegatingAction[T](action: DelegatingGameAction[T])(implicit gameState: GameState): (GameActionResult[T], GameState) = {
    preventActionOrExecute(action)((unwrapChildAction(action, action.delegate, gameState), gameState))
  }

  private def executeDelegateWithChild[T](
    rootAction: DelegatingGameAction[T],
    childAction: GameAction[T],
    initialGameState: GameState)(
    implicit gameState: GameState,
    stops: Stops
  ): Option[(GameActionResult[T], GameState)] = {
    handleDelegatingActionWithChild[T](rootAction, childAction, initialGameState, executeAction(_)(_, stops))
  }

  private def executeDelegateWithFlatMap[T, S](
    rootAction: DelegatingGameAction[T],
    childAction: GameAction[S],
    flatMapFunction: S => GameAction[T],
    initialGameState: GameState)(
    implicit gameState: GameState,
    stops: Stops
  ): Option[(GameActionResult[T], GameState)] = {
    handleDelegatingActionWithFlatMap[T, S](rootAction, childAction, flatMapFunction, initialGameState, executeAction(_)(_, stops))
  }

  private def handleDelegatingActionWithChild[T](
    rootAction: DelegatingGameAction[T],
    childAction: GameAction[T],
    initialGameState: GameState,
    actionExecutor: (GameAction[T], GameState) => Option[(GameActionResult[T], GameState)])(
    implicit gameState: GameState
  ): Option[(GameActionResult[T], GameState)] = {
    def transformResult(result: GameActionResult[T]): GameActionResult[T] = result match {
      case GameActionResult.Value(value) =>
        GameActionResult.Action(PartiallyExecutedActionWithResult(rootAction, value, initialGameState))
      case GameActionResult.Action(childAction) =>
        unwrapChildAction(rootAction, childAction, initialGameState)
      case interrupt: GameActionResult.Interrupted =>
        interrupt
    }
    handleChildAction(rootAction, childAction, initialGameState, actionExecutor, transformResult)
  }

  private def handleDelegatingActionWithFlatMap[T, S](
    rootAction: DelegatingGameAction[T],
    childAction: GameAction[S],
    flatMapFunction: S => GameAction[T],
    initialGameState: GameState,
    actionExecutor: (GameAction[S], GameState) => Option[(GameActionResult[S], GameState)])(
    implicit gameState: GameState
  ): Option[(GameActionResult[T], GameState)] = {
    def transformResult(result: GameActionResult[S]): GameActionResult[T] = result match {
      case GameActionResult.Value(value) =>
        GameActionResult.Action(PartiallyExecutedActionWithFlatMappedResult(rootAction, value, flatMapFunction, initialGameState))
      case GameActionResult.Action(childAction) =>
        unwrapFlatMappedChildAction(rootAction, childAction, flatMapFunction, initialGameState)
      case interrupt: GameActionResult.Interrupted =>
        interrupt
    }
    handleChildAction(rootAction, childAction, initialGameState, actionExecutor, transformResult)
  }

  private def handleChildAction[T, S](
    rootAction: DelegatingGameAction[T],
    childAction: GameAction[S],
    initialGameState: GameState,
    actionExecutor: (GameAction[S], GameState) => Option[(GameActionResult[S], GameState)],
    resultTransformer: GameActionResult[S] => GameActionResult[T])(
    implicit currentGameState: GameState
  ): Option[(GameActionResult[T], GameState)] = {
    actionExecutor(childAction, currentGameState)
      .map(_.mapLeft(resultTransformer))
  }

  @tailrec
  private def unwrapChildAction[T](
    rootAction: DelegatingGameAction[T],
    childAction: GameAction[T],
    initialGameState: GameState)(
    implicit currentGameState: GameState
  ): GameActionResult[T] = {
    childAction match {
      case ConstantAction(value) =>
        GameActionResult.Action(PartiallyExecutedActionWithResult(rootAction, value, initialGameState))
      case CalculatedGameAction(f: (GameState => GameAction[T])) =>
        unwrapChildAction(rootAction, f(currentGameState), initialGameState)
      case FlatMappedGameAction(childAction, f) =>
        unwrapFlatMappedChildAction(rootAction, childAction, f, initialGameState)
      case _ =>
        GameActionResult.Action(PartiallyExecutedActionWithChild(rootAction, childAction, initialGameState))
    }
  }

  @tailrec
  private def unwrapFlatMappedChildAction[T, S](
    rootAction: DelegatingGameAction[T],
    childAction: GameAction[S],
    f: S => GameAction[T],
    initialGameState: GameState)(
    implicit currentGameState: GameState
  ): GameActionResult[T] = {
    childAction match {
      case ConstantAction(value) =>
        unwrapChildAction(rootAction, f(value), initialGameState)
      case CalculatedGameAction(g: (GameState => GameAction[S])) =>
        unwrapFlatMappedChildAction(rootAction, g(currentGameState), f, initialGameState)
      case FlatMappedGameAction(childAction, g: (Any => GameAction[S])) =>
         unwrapFlatMappedChildAction(rootAction, childAction, chainFlatMap(g, f), initialGameState)
      case _ =>
        GameActionResult.Action(PartiallyExecutedActionWithFlatMappedChild(rootAction, childAction, f, initialGameState))
    }
  }

  private def chainFlatMap[S, T, U](f: S => GameAction[T], g: T => GameAction[U]): S => GameAction[U] = { s =>
    for {
      t <- f(s)
      u <- g(t)
    } yield u
  }

  private def updateGameState(gameState: GameState, newGameActionResult: GameActionResult[RootGameAction]): GameState = {
    newGameActionResult match {
        case GameActionResult.Value(nextAction) =>
          gameState.updateAction(nextAction)
        case GameActionResult.Action(nextAction) =>
          gameState.updateAction(nextAction)
        case GameActionResult.Backup(gameStateToReturnTo) =>
          gameStateToReturnTo
        case GameActionResult.GameOver(result) =>
          gameState.copy(currentAction = None, result = Some(result))
    }
  }

  def executeGameObjectAction[T](action: GameObjectAction[T])(implicit currentGameState: GameState): (GameActionResult[T], GameState) = {
    (action match {
      case action: DirectGameObjectAction[_] =>
        executeDirectGameObjectAction(action, Nil)
      case action: DelegatingGameObjectAction =>
        executeDelegatingGameObjectAction(action)
      case GameResultAction(gameResult) =>
        (GameActionResult.GameOver(gameResult), currentGameState)
      case PartiallyExecutedGameObjectAction(gameObjectAction, remainingChildActions, initialGameState) =>
        executePartiallyExecutedDelegatingGameObjectAction(gameObjectAction, remainingChildActions, initialGameState, currentGameState)
    }).asInstanceOf[(GameActionResult[T], GameState)]
  }

  def executeDirectGameObjectAction[T](
    action: DirectGameObjectAction[T],
    appliedReplacementEffects: Seq[ReplacementEffect])(
    implicit currentGameState: GameState
  ): (GameActionResult[Option[T]], GameState) = {
    preventActionOrExecute(action) {
      getApplicableReplacementEffects(action).diff(appliedReplacementEffects).view
        .mapCollect(e => e.replaceAction(action).map(e -> _))
        .headOption match
      {
        case Some((effect, result)) =>
          executeDirectGameObjectAction(result.asInstanceOf[DirectGameObjectAction[T]], appliedReplacementEffects :+ effect)
        case None =>
          action.execute(currentGameState) match {
            case DirectGameObjectAction.Happened(value, gameObjectStateAfterAction) =>
              val gameStateAfterAction = currentGameState.updateGameObjectState(gameObjectStateAfterAction)
              val gameStateWithLogEvent = gameStateAfterAction.recordLogEvent(action.getLogEvent(gameStateAfterAction))
              recordExecutedEvent(action, Some(value), currentGameState, gameStateWithLogEvent)
            case DirectGameObjectAction.DidntHappen =>
              (GameActionResult.Value(None), currentGameState)
          }
      }
    }
  }

  def getApplicableReplacementEffects[T](
    action: DirectGameObjectAction[T])(
    implicit currentGameState: GameState
  ): Seq[ReplacementEffect] = {
    val currentReplacementEffects = currentGameState.gameObjectState.activeContinuousEffects.ofType[ReplacementEffect].toSeq
    val etbReplacementEffects = for {
      action <- action.asOptionalInstanceOf[MoveToBattlefieldAction].toSeq
      DirectGameObjectAction.Happened(newObjectId, expectedGameObjectState) <- action.execute.asOptionalInstanceOf[DirectGameObjectAction.Happened[ObjectId]].toSeq
      expectedObjectState = expectedGameObjectState.derivedState.allObjectStates(newObjectId)
      expectedAbilities = expectedObjectState.characteristics.abilities.ofType[StaticAbility]
      currentObjectState = currentGameState.gameObjectState.derivedState.allObjectStates(action.objectId)
      replacementEffect <- expectedAbilities.flatMap(_.getEffects(EffectContext(currentObjectState))).ofType[EntersTheBattlefieldReplacementEffect]
      if replacementEffect.subjectPhrase == CardName
    } yield replacementEffect
    currentReplacementEffects ++ etbReplacementEffects
  }

  def executeDelegatingGameObjectAction(action: DelegatingGameObjectAction)(implicit currentGameState: GameState): (GameActionResult[Unit], GameState) = {
    preventActionOrExecute(action) {
        (GameActionResult.Action(PartiallyExecutedGameObjectAction(action, action.delegate, currentGameState)), currentGameState)
    }
  }

  def executePartiallyExecutedDelegatingGameObjectAction(action: DelegatingGameObjectAction, childActions: Seq[GameObjectAction[_]], initialGameState: GameState, currentGameState: GameState): (GameActionResult[Unit], GameState) = {
    childActions match {
      case Nil =>
        recordExecutedEvent(action, (), initialGameState, currentGameState)
      case firstChildAction +: remainingChildActions =>
        executeGameObjectAction(firstChildAction)(currentGameState) match {
          case (GameActionResult.Value(_), resultingGameState) =>
            (GameActionResult.Action(PartiallyExecutedGameObjectAction(action, remainingChildActions, initialGameState)), resultingGameState)
          case (GameActionResult.Action(newChildAction: GameObjectAction[_]), resultingGameState) =>
            (GameActionResult.Action(PartiallyExecutedGameObjectAction(action, newChildAction +: remainingChildActions, initialGameState)), resultingGameState)
          case (interrupt: GameActionResult.Interrupted, resultingGameState) =>
            (interrupt, resultingGameState)
        }
    }
  }

  private def preventActionOrExecute[T](
    action: GameAction[T])(
    execute: => (GameActionResult[T], GameState))(
    implicit gameState: GameState
  ): (GameActionResult[T], GameState) = {
    val preventResult = gameState.gameObjectState.activeContinuousEffects.ofType[PreventionEffect]
      .findOption(_.checkAction(action, gameState).asOptionalInstanceOf[Prevent])
    preventResult match {
      case Some(Prevent(logEvent)) =>
        (GameActionResult.Value(().asInstanceOf[T]), gameState.recordLogEvent(logEvent))
      case None =>
        execute
    }
  }

  private def recordExecutedEvent[T](action: GameAction[T], result: T, initialGameState: GameState, finalGameState: GameState): (GameActionResult[T], GameState) = {
      val event = HistoryEvent.ResolvedAction(action, result, initialGameState)
      val triggeredAbilities = getTriggeringAbilities(event, initialGameState, finalGameState)
      val endedEffects = getEndedEffects(event, finalGameState)
      val finalGameObjectState = finalGameState.gameObjectState
        .addWaitingTriggeredAbilities(triggeredAbilities)
        .updateEffects(_.diff(endedEffects))
      val newGameState = finalGameState
        .recordAction(event)
        .updateGameObjectState(finalGameObjectState)
      (GameActionResult.Value(result), newGameState)
  }

  private def getTriggeringAbilities(event: HistoryEvent.ResolvedAction[_], gameStateBeforeAction: GameState, gameStateAfterAction: GameState): Seq[TriggeredAbility] = {
    val abilitiesLookingBack = gameStateBeforeAction.gameObjectState.activeTriggeredAbilities
      .filter(_.looksBackInTime)
      .filter(_.conditionMatchesEvent(event, gameStateBeforeAction))
    val abilitiesLookingForward = gameStateAfterAction.gameObjectState.activeTriggeredAbilities
      .filter(!_.looksBackInTime)
      .filter(_.conditionMatchesEvent(event, gameStateAfterAction))
    (abilitiesLookingBack ++ abilitiesLookingForward).toSeq
  }

  private def getEndedEffects(event: HistoryEvent.ResolvedAction[_], gameStateAfterAction: GameState): Seq[FloatingActiveContinuousEffect] = {
    gameStateAfterAction.gameObjectState.floatingActiveContinuousEffects.filter(effect => {
      def matchesCondition = effect.matchesEndCondition(event, gameStateAfterAction)
      def objectIsGone = effect.effect.asOptionalInstanceOf[CharacteristicOrControlChangingContinuousEffect]
        .exists(e => !gameStateAfterAction.gameObjectState.allObjects.exists(_.objectId == e.affectedObject))
      matchesCondition || objectIsGone
    })
  }
}
