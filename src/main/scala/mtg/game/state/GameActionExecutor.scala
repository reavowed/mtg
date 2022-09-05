package mtg.game.state

import mtg.abilities.{PendingTriggeredAbility, StaticAbility}
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
  @tailrec
  def executeAll(gameState: GameState)(implicit stops: Stops): GameState = {
    execute(gameState, executeNextState(_)(_, stops)) match {
      case Some(newGameState) =>
        executeAll(newGameState)
      case None =>
        gameState
    }
  }

  private def execute(gameState: GameState, f: (GameActionExecutionState.Halting[RootGameAction], GameState) => Option[(GameActionExecutionState[RootGameAction], GameState)]): Option[GameState] = {
    for {
      (newExecutionState, gameStateAfterAction) <- f(gameState.currentActionExecutionState, gameState)
    } yield updateGameState(gameStateAfterAction, newExecutionState)
  }

  def handleDecision(gameState: GameState, serializedDecision: String, actingPlayer: PlayerId)(implicit stops: Stops): Option[GameState] = {
    execute(gameState, handleDecisionForExecutionState(_, serializedDecision, actingPlayer)(_))
      .map(executeAll)
  }

  def handleDecisionForExecutionState[T](executionState: GameActionExecutionState.Halting[T], serializedDecision: String, actingPlayer: PlayerId)(implicit gameState: GameState): Option[(GameActionExecutionState[T], GameState)] = {
    executionState match {
      case GameActionExecutionState.Action(choice: Choice[T]) if choice.playerToAct == actingPlayer =>
        choice.handleDecision(serializedDecision).map(d => (GameActionExecutionState.Value(d), gameState.recordChoice(choice, d)))
      case GameActionExecutionState.DelegatingAction(rootAction, innerExecutionState, initialGameState) =>
        handleDecisionForDelegatedExecutionState(rootAction, innerExecutionState, initialGameState, serializedDecision, actingPlayer)
      case GameActionExecutionState.FlatMapped(innerExecutionState, flatMapFunction) =>
        handleDecisionForExecutionState(innerExecutionState, serializedDecision, actingPlayer).map {
        case (resultingExecutionState: GameActionExecutionState[T], gameState) =>
          (resultingExecutionState.flatMap(flatMapFunction), gameState)
      }
      case _ =>
        None
    }
  }

  def handleDecisionForDelegatedExecutionState[T](
    rootAction: DelegatingGameAction[T],
    innerExecutionState: GameActionExecutionState.Child[T],
    initialGameState: GameState,
    serializedDecision: String,
    actingPlayer: PlayerId)(
    implicit gameState: GameState
  ): Option[(GameActionExecutionState[T], GameState)] = {
    handleDelegatingActionWithChild[T](rootAction, innerExecutionState, initialGameState, handleDecisionForExecutionState(_, serializedDecision, actingPlayer)(_))
  }

  def executeNextState[T](executionState: GameActionExecutionState.Halting[T])(implicit gameState: GameState, stops: Stops): Option[(GameActionExecutionState[T], GameState)] = executionState match {
    case GameActionExecutionState.Action(gameAction) =>
      executeAction(gameAction)
    case GameActionExecutionState.DelegatingAction(gameAction, innerExecutionState, initialGameState) =>
      executeInsideDelegatingAction(gameAction, innerExecutionState, initialGameState)
    case GameActionExecutionState.FlatMapped(innerExecutionState, flatMapFunction) =>
      executeNextState(innerExecutionState).map {
        case (resultingExecutionState: GameActionExecutionState[T], gameState) =>
          (resultingExecutionState.flatMap(flatMapFunction), gameState)
      }
    case GameActionExecutionState.Result(_) =>
      None
  }

  private def executeAction[T](gameAction: GameAction[T])(implicit gameState: GameState, stops: Stops): Option[(GameActionExecutionState[T], GameState)] = gameAction match {
    case priorityChoice: PriorityChoice if stops.shouldAutoPass(priorityChoice, gameState) =>
      handleDecisionForExecutionState(GameActionExecutionState.Action(priorityChoice), "Pass", priorityChoice.playerToAct)
    case _: Choice[T] =>
      None
    case LogEventAction(logEvent) =>
      Some((GameActionExecutionState.Value(().asInstanceOf[T]), gameState.recordLogEvent(logEvent)))
    case calculatedGameAction: CalculatedGameAction[T] =>
      Some((GameActionExecutionState.Action(calculatedGameAction.f(gameState)), gameState))
    case gameObjectAction: GameObjectAction[T] =>
      Some(executeGameObjectAction(gameObjectAction))
    case action: DelegatingGameAction[T] =>
      Some(executeDelegatingAction(action))
    case constantAction: ConstantAction[T] =>
      Some((GameActionExecutionState.Value(constantAction.value), gameState))
    case FlatMappedGameAction(innerAction, flatMapFunction) =>
      Some((GameActionExecutionState.FlatMapped(GameActionExecutionState.Action(innerAction), flatMapFunction), gameState))
  }

  private def executeDelegatingAction[T](action: DelegatingGameAction[T])(implicit gameState: GameState): (GameActionExecutionState[T], GameState) = {
    preventActionOrExecute(action)((
      GameActionExecutionState.DelegatingAction(action, GameActionExecutionState.Action(action.delegate), gameState),
      gameState))
  }

  private def executeInsideDelegatingAction[T](
    rootAction: DelegatingGameAction[T],
    innerExecutionState: GameActionExecutionState.Halting[T],
    initialGameState: GameState)(
    implicit gameState: GameState,
    stops: Stops
  ): Option[(GameActionExecutionState[T], GameState)] = {
    handleDelegatingActionWithChild[T](rootAction, innerExecutionState, initialGameState, executeNextState(_)(_, stops))
  }

  private def handleDelegatingActionWithChild[T](
    rootAction: DelegatingGameAction[T],
    innerExecutionState: GameActionExecutionState.Halting[T],
    initialGameState: GameState,
    innerExecutor: (GameActionExecutionState.Halting[T], GameState) => Option[(GameActionExecutionState[T], GameState)])(
    implicit gameState: GameState
  ): Option[(GameActionExecutionState[T], GameState)] = {
    def transformResult(result: GameActionExecutionState[T], gameState: GameState): (GameActionExecutionState[T], GameState) = result match {
      case GameActionExecutionState.Value(value) =>
        recordExecutedEvent(rootAction, value, initialGameState, gameState)
      case interrupt: GameActionExecutionState.Interrupt =>
        (interrupt, gameState)
      case other: GameActionExecutionState.Child[T] =>
        (GameActionExecutionState.DelegatingAction(rootAction, other, initialGameState), gameState)
    }
    handleChildAction(innerExecutionState, innerExecutor, transformResult)
  }

  private def handleDelegatingActionWithFlatMap[T, S](
    rootAction: DelegatingGameAction[T],
    innerExecutionState: GameActionExecutionState.Halting[S],
    initialGameState: GameState,
    flatMapFunction: S => GameAction[T],
    innerExecutor: (GameActionExecutionState.Halting[S], GameState) => Option[(GameActionExecutionState[S], GameState)])(
    implicit gameState: GameState
  ): Option[(GameActionExecutionState[T], GameState)] = {
    def transformResult(result: GameActionExecutionState[S], gameState: GameState): (GameActionExecutionState[T], GameState) = result match {
      case GameActionExecutionState.Value(value) =>
        (GameActionExecutionState.DelegatingAction(rootAction, GameActionExecutionState.Action(flatMapFunction(value)), initialGameState), gameState)
      case interrupt: GameActionExecutionState.Interrupt =>
        (interrupt, gameState)
      case other: GameActionExecutionState.Child[T] =>
        (GameActionExecutionState.DelegatingAction(rootAction, GameActionExecutionState.FlatMapped(other, flatMapFunction), initialGameState), gameState)
    }
    handleChildAction(innerExecutionState, innerExecutor, transformResult)
  }

  private def handleChildAction[T, S](
    innerExecutionState: GameActionExecutionState.Halting[T],
    innerExecutor: (GameActionExecutionState.Halting[T], GameState) => Option[(GameActionExecutionState[T], GameState)],
    resultTransformer: (GameActionExecutionState[T], GameState) => (GameActionExecutionState[S], GameState))(
    implicit currentGameState: GameState
  ): Option[(GameActionExecutionState[S], GameState)] = {
    innerExecutor(innerExecutionState, currentGameState).map(resultTransformer.tupled)
  }

  private def updateGameState(gameState: GameState, newExecutionState: GameActionExecutionState[RootGameAction]): GameState = {
    newExecutionState match {
        case GameActionExecutionState.Value(nextAction) =>
          gameState.updateActionExecutionState(GameActionExecutionState.Action(nextAction))
        case halting: GameActionExecutionState.Halting[RootGameAction] =>
          gameState.updateActionExecutionState(halting)
        case GameActionExecutionState.Backup(gameState) =>
          gameState
    }
  }

  def executeGameObjectAction[T](action: GameObjectAction[T])(implicit currentGameState: GameState): (GameActionExecutionState[T], GameState) = {
    (action match {
      case action: DirectGameObjectAction[_] =>
        executeDirectGameObjectAction(action, Nil)
      case action: DelegatingGameObjectAction =>
        executeDelegatingGameObjectAction(action)
      case GameResultAction(gameResult) =>
        (GameActionExecutionState.Result(gameResult), currentGameState)
      case PartiallyExecutedGameObjectAction(gameObjectAction, remainingChildActions, initialGameState) =>
        executePartiallyExecutedDelegatingGameObjectAction(gameObjectAction, remainingChildActions, initialGameState, currentGameState)
    }).asInstanceOf[(GameActionExecutionState[T], GameState)]
  }

  def executeDirectGameObjectAction[T](
    action: DirectGameObjectAction[T],
    appliedReplacementEffects: Seq[ReplacementEffect])(
    implicit currentGameState: GameState
  ): (GameActionExecutionState[Option[T]], GameState) = {
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
              (GameActionExecutionState.Value(None), currentGameState)
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

  def executeDelegatingGameObjectAction(action: DelegatingGameObjectAction)(implicit currentGameState: GameState): (GameActionExecutionState[Unit], GameState) = {
    preventActionOrExecute(action) {
        (GameActionExecutionState.Action(PartiallyExecutedGameObjectAction(action, action.delegate, currentGameState)), currentGameState)
    }
  }

  def executePartiallyExecutedDelegatingGameObjectAction(action: DelegatingGameObjectAction, childActions: Seq[GameObjectAction[_]], initialGameState: GameState, currentGameState: GameState): (GameActionExecutionState[Unit], GameState) = {
    childActions match {
      case Nil =>
        recordExecutedEvent(action, (), initialGameState, currentGameState)
      case firstChildAction +: remainingChildActions =>
        executeGameObjectAction(firstChildAction)(currentGameState) match {
          case (GameActionExecutionState.Value(_), resultingGameState) =>
            (GameActionExecutionState.Action(PartiallyExecutedGameObjectAction(action, remainingChildActions, initialGameState)), resultingGameState)
          case (GameActionExecutionState.Action(newChildAction: GameObjectAction[_]), resultingGameState) =>
            (GameActionExecutionState.Action(PartiallyExecutedGameObjectAction(action, newChildAction +: remainingChildActions, initialGameState)), resultingGameState)
          case (interrupt: GameActionExecutionState.Interrupt, resultingGameState) =>
            (interrupt, resultingGameState)
        }
    }
  }

  private def preventActionOrExecute[T](
    action: GameAction[T])(
    execute: => (GameActionExecutionState[T], GameState))(
    implicit gameState: GameState
  ): (GameActionExecutionState[T], GameState) = {
    val preventResult = gameState.gameObjectState.activeContinuousEffects.ofType[PreventionEffect]
      .findOption(_.checkAction(action, gameState).asOptionalInstanceOf[Prevent])
    preventResult match {
      case Some(Prevent(logEvent)) =>
        (GameActionExecutionState.Value(().asInstanceOf[T]), gameState.recordLogEvent(logEvent))
      case None =>
        execute
    }
  }

  private def recordExecutedEvent[T](action: GameAction[T], result: T, initialGameState: GameState, finalGameState: GameState): (GameActionExecutionState[T], GameState) = {
      val event = HistoryEvent.ResolvedAction(action, result, initialGameState)
      val triggeredAbilities = getTriggeringAbilities(event, initialGameState, finalGameState)
      val endedEffects = getEndedEffects(event, finalGameState)
      val finalGameObjectState = finalGameState.gameObjectState
        .addWaitingTriggeredAbilities(triggeredAbilities)
        .updateEffects(_.diff(endedEffects))
      val newGameState = finalGameState
        .recordAction(event)
        .updateGameObjectState(finalGameObjectState)
      (GameActionExecutionState.Value(result), newGameState)
  }

  private def getTriggeringAbilities(event: HistoryEvent.ResolvedAction[_], gameStateBeforeAction: GameState, gameStateAfterAction: GameState): Seq[Int => PendingTriggeredAbility] = {
    val abilitiesLookingBack = gameStateBeforeAction.gameObjectState.activeTriggeredAbilities
      .filter(_.looksBackInTime)
      .mapCollect(_.matchEvent(event, gameStateBeforeAction))
    val abilitiesLookingForward = gameStateAfterAction.gameObjectState.activeTriggeredAbilities
      .filter(!_.looksBackInTime)
      .mapCollect(_.matchEvent(event, gameStateAfterAction))
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
