package mtg.game.state

import mtg.abilities.TriggeredAbility
import mtg.continuousEffects.PreventionEffect.Result.Prevent
import mtg.continuousEffects.{CharacteristicOrControlChangingContinuousEffect, FloatingActiveContinuousEffect, PreventionEffect}
import mtg.core.PlayerId
import mtg.game.priority.PriorityChoice
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
      Some(executeGameObjectAction(gameObjectAction, gameState))
    case LogEventAction(logEvent) =>
      Some((GameActionResult.Value(()).asInstanceOf[GameActionResult[T]], gameState.recordLogEvent(logEvent)))
  }

  private def executeDelegatingAction[T](action: DelegatingGameAction[T])(implicit gameState: GameState): (GameActionResult[T], GameState) = {
    val preventResult = gameState.gameObjectState.activeContinuousEffects.ofType[PreventionEffect]
      .findOption(_.checkAction(action, gameState).asOptionalInstanceOf[Prevent])
    preventResult match {
      case Some(Prevent(logEvent)) =>
        (GameActionResult.Value(().asInstanceOf[T]), gameState.recordLogEvent(logEvent))
      case None =>
        unwrapAndRecord(action, action.delegate, gameState)
    }
  }

  private def unwrapAndRecord[T](action: DelegatingGameAction[T], childAction: GameAction[T], initialGameState: GameState)(implicit gameState: GameState): (GameActionResult[T], GameState) = {
    val actionResult = unwrapDelegateAction(action, childAction)
    val newGameState = actionResult match {
      case GameActionResult.Value(v) =>
        val event = HistoryEvent.ResolvedAction(action, v, initialGameState)
        gameState
          .recordAction(event)
          .updateGameObjectState(_.addWaitingTriggeredAbilities(getTriggeringAbilities(event, gameState)))
      case _ =>
        gameState
    }
    (actionResult, newGameState)
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

  def executeGameObjectAction[T](action: GameObjectAction[T], initialGameState: GameState): (GameActionResult[T], GameState) = {
    executeGameObjectActions(Seq(action), initialGameState).mapLeft(_.asInstanceOf[GameActionResult[T]])
  }

  @tailrec
  def executeGameObjectActions(actions: Seq[GameObjectAction[_]], initialGameState: GameState): (GameActionResult[Unit], GameState) = {
    actions match {
      case action +: nextActions =>
        val preventResult = initialGameState.gameObjectState.activeContinuousEffects.ofType[PreventionEffect]
          .findOption(_.checkAction(action, initialGameState).asOptionalInstanceOf[Prevent])
        preventResult match {
          case Some(Prevent(logEvent)) =>
            (GameActionResult.Value(()), initialGameState.recordLogEvent(logEvent))
          case None =>
            action match {
              case action: DirectGameObjectAction =>
                val gameObjectStateAfterAction = action.execute(initialGameState)
                val gameStateAfterAction = initialGameState.updateGameObjectState(gameObjectStateAfterAction)
                val event = HistoryEvent.ResolvedAction(action, (), initialGameState)
                val triggeredAbilities = getTriggeringAbilities(event, gameStateAfterAction)
                val endedEffects = getEndedEffects(event, gameStateAfterAction)
                val finalGameObjectState = gameObjectStateAfterAction
                  .addWaitingTriggeredAbilities(triggeredAbilities)
                  .updateEffects(_.diff(endedEffects))
                val newGameState = initialGameState
                  .recordAction(event)
                  .updateGameObjectState(finalGameObjectState)
                  .recordLogEvent(action.getLogEvent(initialGameState))
                executeGameObjectActions(nextActions, newGameState)
              case action: DelegatingGameObjectAction =>
                executeGameObjectActions(action.delegate(initialGameState) ++ nextActions, initialGameState)
              case GameResultAction(gameResult) =>
                (GameActionResult.GameOver(gameResult), initialGameState)
            }
        }
      case Nil =>
        (GameActionResult.Value(()), initialGameState)
    }
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
