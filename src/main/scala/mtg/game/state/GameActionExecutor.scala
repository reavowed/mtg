package mtg.game.state

import mtg.abilities.TriggeredAbility
import mtg.continuousEffects.PreventionEffect.Result.Prevent
import mtg.continuousEffects.{CharacteristicOrControlChangingContinuousEffect, FloatingActiveContinuousEffect, PreventionEffect}
import mtg.core.PlayerId
import mtg.game.priority.PriorityChoice
import mtg.game.state.GameActionExecutor.preventActionOrExecute
import mtg.game.state.history.HistoryEvent

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
      case PartiallyExecutedActionWithDelegate(rootAction, child) =>
        handleDecisionForDelegate(rootAction, child, serializedDecision, actingPlayer)
      case PartiallyExecutedActionWithFlatMap(rootAction, child, f) =>
        handleDecisionForDelegateWithFlatMap(rootAction, child, f, serializedDecision, actingPlayer)
      case _ =>
        None
    }
  }

  def handleDecisionForDelegate[T](
    rootAction: DelegatingGameAction[T],
    childAction: GameAction[T],
    serializedDecision: String,
    actingPlayer: PlayerId)(
    implicit gameState: GameState
  ): Option[(GameActionResult[T], GameState)] = {
    handleDelegatingActionWithChild[T](rootAction, childAction, handleDecisionForAction(_, serializedDecision, actingPlayer)(_))
  }

  def handleDecisionForDelegateWithFlatMap[T, S](
    rootAction: DelegatingGameAction[T],
    childAction: GameAction[S],
    f: S => GameAction[T],
    serializedDecision: String,
    actingPlayer: PlayerId)(
    implicit gameState: GameState
  ): Option[(GameActionResult[T], GameState)] = {
    handleDelegatingActionWithFlatMap[T, S](rootAction, childAction, f, handleDecisionForAction(_, serializedDecision, actingPlayer)(_))
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
    case PartiallyExecutedActionWithDelegate(rootAction, childAction) =>
      executeDelegateWithChild(rootAction, childAction)
    case PartiallyExecutedActionWithFlatMap(rootAction, childAction, f) =>
      executeDelegateWithFlatMap(rootAction, childAction, f)
    case gameObjectAction: GameObjectAction[T] =>
      Some(executeGameObjectAction(gameObjectAction))
    case LogEventAction(logEvent) =>
      Some((GameActionResult.Value(()).asInstanceOf[GameActionResult[T]], gameState.recordLogEvent(logEvent)))
  }

  private def executeDelegatingAction[T](action: DelegatingGameAction[T])(implicit gameState: GameState): (GameActionResult[T], GameState) = {
    preventActionOrExecute(action)(unwrapAndRecord(action, action.delegate, gameState))
  }

  private def unwrapAndRecord[T](action: DelegatingGameAction[T], childAction: GameAction[T], initialGameState: GameState)(implicit gameState: GameState): (GameActionResult[T], GameState) = {
    val actionResult = unwrapDelegateAction(action, childAction)
    actionResult match {
      case GameActionResult.Value(v) =>
        recordExecutedEvent(action, v, initialGameState, gameState)
      case _ =>
        (actionResult, gameState)
    }
  }

  private def executeDelegateWithChild[T](
    rootAction: DelegatingGameAction[T],
    childAction: GameAction[T])(
    implicit gameState: GameState,
    stops: Stops
  ): Option[(GameActionResult[T], GameState)] = {
    handleDelegatingActionWithChild[T](rootAction, childAction, executeAction(_)(_, stops))
  }

  private def executeDelegateWithFlatMap[T, S](
    rootAction: DelegatingGameAction[T],
    childAction: GameAction[S],
    flatMapFunction: S => GameAction[T])(
    implicit gameState: GameState,
    stops: Stops
  ): Option[(GameActionResult[T], GameState)] = {
    handleDelegatingActionWithFlatMap[T, S](rootAction, childAction, flatMapFunction, executeAction(_)(_, stops))
  }

  private def handleDelegatingActionWithChild[T](
    rootAction: DelegatingGameAction[T],
    childAction: GameAction[T],
    actionExecutor: (GameAction[T], GameState) => Option[(GameActionResult[T], GameState)])(
    implicit gameState: GameState
  ): Option[(GameActionResult[T], GameState)] = {
    handleDelegatingAction(rootAction, childAction, actionExecutor, identity[GameActionResult[T]])
  }

  private def handleDelegatingActionWithFlatMap[T, S](
    rootAction: DelegatingGameAction[T],
    childAction: GameAction[S],
    flatMapFunction: S => GameAction[T],
    actionExecutor: (GameAction[S], GameState) => Option[(GameActionResult[S], GameState)])(
    implicit gameState: GameState
  ): Option[(GameActionResult[T], GameState)] = {
    def transformResult(result: GameActionResult[S]): GameActionResult[T] = result match {
      case GameActionResult.Action(childAction) =>
        GameActionResult.Action(PartiallyExecutedActionWithFlatMap(rootAction, childAction, flatMapFunction))
      case GameActionResult.Value(value) =>
        GameActionResult.Action(flatMapFunction(value))
      case interrupt: GameActionResult.Interrupted =>
        interrupt
    }
    handleDelegatingAction(rootAction, childAction, actionExecutor, transformResult)
  }

  private def handleDelegatingAction[T, S](
    rootAction: DelegatingGameAction[T],
    childAction: GameAction[S],
    actionExecutor: (GameAction[S], GameState) => Option[(GameActionResult[S], GameState)],
    resultTransformer: GameActionResult[S] => GameActionResult[T])(
    implicit initialGameState: GameState
  ): Option[(GameActionResult[T], GameState)] = {
    actionExecutor(childAction, initialGameState)
      .map(_.mapLeft(resultTransformer))
      .map {
        case (GameActionResult.Value(value), gameState: GameState) =>
          unwrapAndRecord(rootAction, ConstantAction(value), initialGameState)(gameState)
        case (GameActionResult.Action(childAction), gameState: GameState) =>
          unwrapAndRecord(rootAction, childAction, initialGameState)(gameState)
        case (result, gameState) =>
          (result, gameState)
      }
  }


  @tailrec
  private def unwrapDelegateAction[T](
    rootAction: DelegatingGameAction[T],
    childAction: GameAction[T])(
    implicit gameState: GameState
  ): GameActionResult[T] = {
    childAction match {
      case ConstantAction(value) =>
        GameActionResult.Value(value)
      case CalculatedGameAction(f: (GameState => GameAction[T])) =>
        unwrapDelegateAction(rootAction, f(gameState))
      case FlatMappedGameAction(childAction, f) =>
        unwrapFlatMappedDelegateAction(rootAction, childAction, f)
      case _ =>
        GameActionResult.Action(PartiallyExecutedActionWithDelegate(rootAction, childAction))
    }
  }

  @tailrec
  private def unwrapFlatMappedDelegateAction[T, S](
    rootAction: DelegatingGameAction[T],
    childAction: GameAction[S],
    f: S => GameAction[T])(
    implicit gameState: GameState
  ): GameActionResult[T] = {
    childAction match {
      case ConstantAction(value) =>
        unwrapDelegateAction(rootAction, f(value))
      case CalculatedGameAction(g: (GameState => GameAction[S])) =>
        unwrapFlatMappedDelegateAction(rootAction, g(gameState), f)
      case FlatMappedGameAction(childAction, g: (Any => GameAction[S])) =>
         unwrapFlatMappedDelegateAction(rootAction, childAction, (x: Any) => for { s <- g(x); t <- f(s) } yield t)
      case _ =>
        GameActionResult.Action(PartiallyExecutedActionWithFlatMap(rootAction, childAction, f))
    }
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
      case action: DirectGameObjectAction =>
        executeDirectGameObjectAction(action)
      case action: DelegatingGameObjectAction =>
        executeDelegatingGameObjectAction(action)
      case GameResultAction(gameResult) =>
        (GameActionResult.GameOver(gameResult), currentGameState)
      case PartiallyExecutedGameObjectAction(gameObjectAction, remainingChildActions, initialGameState) =>
        executePartiallyExecutedDelegatingGameObjectAction(gameObjectAction, remainingChildActions, initialGameState, currentGameState)
    }).asInstanceOf[(GameActionResult[T], GameState)]
  }

  def executeDirectGameObjectAction[T](action: DirectGameObjectAction)(implicit currentGameState: GameState): (GameActionResult[Unit], GameState) = {
    preventActionOrExecute(action) {
      val gameObjectStateAfterAction = action.execute(currentGameState)
      val gameStateAfterAction = currentGameState.updateGameObjectState(gameObjectStateAfterAction)
      val gameStateWithLogEvent = gameStateAfterAction.recordLogEvent(action.getLogEvent(gameStateAfterAction))
      recordExecutedEvent(action, (), currentGameState, gameStateWithLogEvent)
    }
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
      val event = HistoryEvent.ResolvedAction(action, (), initialGameState)
      val triggeredAbilities = getTriggeringAbilities(event, finalGameState)
      val endedEffects = getEndedEffects(event, finalGameState)
      val finalGameObjectState = finalGameState.gameObjectState
        .addWaitingTriggeredAbilities(triggeredAbilities)
        .updateEffects(_.diff(endedEffects))
      val newGameState = finalGameState
        .recordAction(event)
        .updateGameObjectState(finalGameObjectState)
      (GameActionResult.Value(result), newGameState)
  }

  private def getTriggeringAbilities(event: HistoryEvent.ResolvedAction[_], gameStateAfterAction: GameState): Seq[TriggeredAbility] = {
    gameStateAfterAction.gameObjectState.activeTriggeredAbilities
      .filter(_.conditionMatchesEvent(event, gameStateAfterAction))
      .toSeq
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
