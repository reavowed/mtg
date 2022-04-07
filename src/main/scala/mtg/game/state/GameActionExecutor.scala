package mtg.game.state

import mtg.abilities.TriggeredAbility
import mtg.continuousEffects.PreventionEffect.Result.Prevent
import mtg.continuousEffects.{CharacteristicOrControlChangingContinuousEffect, FloatingActiveContinuousEffect, PreventionEffect}
import mtg.core.PlayerId
import mtg.game.priority.PriorityChoice

import scala.annotation.tailrec

object GameActionExecutor {

  sealed trait ProcessedGameActionResult[+T]
  object ProcessedGameActionResult {
    case class Value[T](value: T) extends ProcessedGameActionResult[T]
    case class Action[T](action: GameAction[T]) extends ProcessedGameActionResult[T]
    sealed trait Interrupted extends ProcessedGameActionResult[Nothing]
    case class Backup(gameState: GameState) extends Interrupted
    case class GameOver(gameResult: GameResult) extends Interrupted
  }

  def handleDecision(gameState: GameState, serializedDecision: String, actingPlayer: PlayerId)(implicit stops: Stops): Option[GameState] = {
    runAction(gameState, handleDecisionForAction(_, serializedDecision, actingPlayer)(_))
      .map(executeAllActions)
  }

  def handleDecisionForAction[T](action: GameAction[T], serializedDecision: String, actingPlayer: PlayerId)(implicit gameState: GameState): Option[(ProcessedGameActionResult[T], GameState)] = {
    action match {
      case directChoice: Choice[T] if directChoice.playerToAct == actingPlayer =>
        directChoice.handleDecision(serializedDecision).map(d => (ProcessedGameActionResult.Value(d), gameState.recordChoice(directChoice, d)))
      case PartiallyExecutedActionWithChild(rootAction, child, callback) =>
        handleDecisionForChild(rootAction, child, callback, serializedDecision, actingPlayer)
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
  ): Option[(ProcessedGameActionResult[T], GameState)] = {
    handleDelegatingActionWithChild[T](rootAction, childAction, handleDecisionForAction(_, serializedDecision, actingPlayer)(_))
  }

  def handleDecisionForDelegateWithFlatMap[T, S](
    rootAction: DelegatingGameAction[T],
    childAction: GameAction[S],
    f: S => GameAction[T],
    serializedDecision: String,
    actingPlayer: PlayerId)(
    implicit gameState: GameState
  ): Option[(ProcessedGameActionResult[T], GameState)] = {
    handleDelegatingActionWithFlatMap[T, S](rootAction, childAction, f, handleDecisionForAction(_, serializedDecision, actingPlayer)(_))
  }

  private def handleDecisionForChild[T, S](
    rootAction: GameAction[T],
    childAction: GameAction[S],
    callback: (S, GameState) => PartialGameActionResult[T],
    serializedDecision: String,
    actingPlayer: PlayerId)(
    implicit gameState: GameState
  ): Option[(ProcessedGameActionResult[T], GameState)] = {
    runChildAction[T, S](rootAction, childAction, callback, handleDecisionForAction[S](_, serializedDecision, actingPlayer)(_))
  }

  private def runAction(gameState: GameState, f: (GameAction[RootGameAction], GameState) => Option[(ProcessedGameActionResult[RootGameAction], GameState)]): Option[GameState] = {
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

  def executeAction[T](gameAction: GameAction[T])(implicit gameState: GameState, stops: Stops): Option[(ProcessedGameActionResult[T], GameState)] = gameAction match {
    case priorityChoice: PriorityChoice if stops.shouldAutoPass(priorityChoice, gameState) =>
      handleDecisionForAction(priorityChoice, "Pass", priorityChoice.playerToAct)
    case _: Choice[T] =>
      None
    case action: ExecutableGameAction[T] =>
      Some(executeExecutableAction(action))
    case action: DelegatingGameAction[T] =>
      Some(executeDelegatingAction(action))
    case PartiallyExecutedActionWithDelegate(rootAction, childAction) =>
      executeDelegateWithChild(rootAction, childAction)
    case PartiallyExecutedActionWithFlatMap(rootAction, childAction, f) =>
      executeDelegateWithFlatMap(rootAction, childAction, f)
    case PartiallyExecutedActionWithValue(rootAction, value, callback) =>
      Some(executeCallback(rootAction, value, callback))
    case PartiallyExecutedActionWithChild(rootAction, childAction, callback) =>
      executeChildAction(rootAction, childAction, callback)
    case gameObjectAction: GameObjectAction[T] =>
      Some(executeGameObjectAction(gameObjectAction, gameState))
    case LogEventAction(logEvent) =>
      Some((ProcessedGameActionResult.Value(()).asInstanceOf[ProcessedGameActionResult[T]], gameState.recordLogEvent(logEvent)))
  }

  private def executeExecutableAction[T](action: ExecutableGameAction[T])(implicit gameState: GameState):  (ProcessedGameActionResult[T], GameState) = {
    val preventResult = gameState.gameObjectState.activeContinuousEffects.ofType[PreventionEffect]
      .findOption(_.checkAction(action, gameState).asOptionalInstanceOf[Prevent])
    preventResult match {
      case Some(Prevent(logEvent)) =>
        (ProcessedGameActionResult.Value(().asInstanceOf[T]), gameState.recordLogEvent(logEvent))
      case None =>
        handleActionResult(action, action.execute()).mapRight { finalGameState =>
          val triggeredAbilities = getTriggeringAbilities(action, finalGameState)
          finalGameState.updateGameObjectState(_.addWaitingTriggeredAbilities(triggeredAbilities))
        }
    }
  }

  private def executeDelegatingAction[T](action: DelegatingGameAction[T])(implicit gameState: GameState): (ProcessedGameActionResult[T], GameState) = {
    val preventResult = gameState.gameObjectState.activeContinuousEffects.ofType[PreventionEffect]
      .findOption(_.checkAction(action, gameState).asOptionalInstanceOf[Prevent])
    preventResult match {
      case Some(Prevent(logEvent)) =>
        (ProcessedGameActionResult.Value(().asInstanceOf[T]), gameState.recordLogEvent(logEvent))
      case None =>
        unwrapAndRecord(action, action.delegate)
    }
  }

  private def unwrapAndRecord[T](action: DelegatingGameAction[T], childAction: GameAction[T])(implicit gameState: GameState): (ProcessedGameActionResult[T], GameState) = {
    val actionResult = unwrapDelegateAction(action, childAction)
    val newGameState = actionResult match {
      case ProcessedGameActionResult.Value(v) =>
        gameState.recordAction(action, v).updateGameObjectState(_.addWaitingTriggeredAbilities(getTriggeringAbilities(action, gameState)))
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
  ): Option[(ProcessedGameActionResult[T], GameState)] = {
    handleDelegatingActionWithChild[T](rootAction, childAction, executeAction(_)(_, stops))
  }

  private def executeDelegateWithFlatMap[T, S](
    rootAction: DelegatingGameAction[T],
    childAction: GameAction[S],
    flatMapFunction: S => GameAction[T])(
    implicit gameState: GameState,
    stops: Stops
  ): Option[(ProcessedGameActionResult[T], GameState)] = {
    handleDelegatingActionWithFlatMap[T, S](rootAction, childAction, flatMapFunction, executeAction(_)(_, stops))
  }

  private def handleDelegatingActionWithChild[T](
    rootAction: DelegatingGameAction[T],
    childAction: GameAction[T],
    actionExecutor: (GameAction[T], GameState) => Option[(ProcessedGameActionResult[T], GameState)])(
    implicit gameState: GameState
  ): Option[(ProcessedGameActionResult[T], GameState)] = {
    handleDelegatingAction(rootAction, childAction, actionExecutor, identity[ProcessedGameActionResult[T]])
  }

  private def handleDelegatingActionWithFlatMap[T, S](
    rootAction: DelegatingGameAction[T],
    childAction: GameAction[S],
    flatMapFunction: S => GameAction[T],
    actionExecutor: (GameAction[S], GameState) => Option[(ProcessedGameActionResult[S], GameState)])(
    implicit gameState: GameState
  ): Option[(ProcessedGameActionResult[T], GameState)] = {
    def transformResult(result: ProcessedGameActionResult[S]): ProcessedGameActionResult[T] = result match {
      case ProcessedGameActionResult.Action(childAction) =>
        ProcessedGameActionResult.Action(PartiallyExecutedActionWithFlatMap(rootAction, childAction, flatMapFunction))
      case ProcessedGameActionResult.Value(value) =>
        ProcessedGameActionResult.Action(flatMapFunction(value))
      case interrupt: ProcessedGameActionResult.Interrupted =>
        interrupt
    }
    handleDelegatingAction(rootAction, childAction, actionExecutor, transformResult)
  }

  private def handleDelegatingAction[T, S](
    rootAction: DelegatingGameAction[T],
    childAction: GameAction[S],
    actionExecutor: (GameAction[S], GameState) => Option[(ProcessedGameActionResult[S], GameState)],
    resultTransformer: ProcessedGameActionResult[S] => ProcessedGameActionResult[T])(
    implicit gameState: GameState
  ): Option[(ProcessedGameActionResult[T], GameState)] = {
    actionExecutor(childAction, gameState)
      .map(_.mapLeft(resultTransformer))
      .map {
        case (ProcessedGameActionResult.Value(value), gameState: GameState) =>
          unwrapAndRecord(rootAction, ConstantAction(value))(gameState)
        case (ProcessedGameActionResult.Action(childAction), gameState: GameState) =>
          unwrapAndRecord(rootAction, childAction)(gameState)
        case (result, gameState) =>
          (result, gameState)
      }
  }


  @tailrec
  private def unwrapDelegateAction[T](
    rootAction: DelegatingGameAction[T],
    childAction: GameAction[T])(
    implicit gameState: GameState
  ): ProcessedGameActionResult[T] = {
    childAction match {
      case ConstantAction(value) =>
        ProcessedGameActionResult.Value(value)
      case CalculatedGameAction(f: (GameState => GameAction[T])) =>
        unwrapDelegateAction(rootAction, f(gameState))
      case FlatMappedGameAction(childAction, f) =>
        unwrapFlatMappedDelegateAction(rootAction, childAction, f)
      case _ =>
        ProcessedGameActionResult.Action(PartiallyExecutedActionWithDelegate(rootAction, childAction))
    }
  }

  @tailrec
  private def unwrapFlatMappedDelegateAction[T, S](
    rootAction: DelegatingGameAction[T],
    childAction: GameAction[S],
    f: S => GameAction[T])(
    implicit gameState: GameState
  ): ProcessedGameActionResult[T] = {
    childAction match {
      case ConstantAction(value) =>
        unwrapDelegateAction(rootAction, f(value))
      case CalculatedGameAction(g: (GameState => GameAction[S])) =>
        unwrapFlatMappedDelegateAction(rootAction, g(gameState), f)
      case FlatMappedGameAction(childAction, g: (Any => GameAction[S])) =>
         unwrapFlatMappedDelegateAction(rootAction, childAction, (x: Any) => for { s <- g(x); t <- f(s) } yield t)
      case _ =>
        ProcessedGameActionResult.Action(PartiallyExecutedActionWithFlatMap(rootAction, childAction, f))
    }
  }



  private def executeCallback[T, S](
    rootAction: GameAction[T],
    childValue: S,
    callback: (S, GameState) => PartialGameActionResult[T])(
    implicit gameState: GameState
  ): (ProcessedGameActionResult[T], GameState) = {
    handleActionResult(rootAction, callback(childValue, gameState))
  }

  private def executeChildAction[T, S](
    rootAction: GameAction[T],
    childAction: GameAction[S],
    callback: (S, GameState) => PartialGameActionResult[T])(
    implicit gameState: GameState,
    stops: Stops
  ): Option[(ProcessedGameActionResult[T], GameState)] = {
    runChildAction[T, S](rootAction, childAction, callback, executeAction[S](_)(_, stops))
  }

  private def runChildAction[T, S](
    rootAction: GameAction[T],
    childAction: GameAction[S],
    callback: (S, GameState) => PartialGameActionResult[T],
    f: (GameAction[S], GameState) => Option[(ProcessedGameActionResult[S], GameState)])(
    implicit gameState: GameState
  ): Option[(ProcessedGameActionResult[T], GameState)] = {
    f(childAction, gameState) map {
      case (ProcessedGameActionResult.Value(value), newGameState) =>
        (ProcessedGameActionResult.Action(PartiallyExecutedActionWithValue(rootAction, value, callback)), newGameState)
      case (ProcessedGameActionResult.Action(action), newGameState) =>
        (ProcessedGameActionResult.Action(PartiallyExecutedActionWithChild(rootAction, action, callback)), newGameState)
      case (result: ProcessedGameActionResult.Interrupted, newGameState) =>
        (result, newGameState)
    }
  }

  private def updateGameState(gameState: GameState, newGameActionResult: ProcessedGameActionResult[RootGameAction]): GameState = {
    newGameActionResult match {
        case ProcessedGameActionResult.Value(nextAction) =>
          gameState.updateAction(nextAction)
        case ProcessedGameActionResult.Action(nextAction) =>
          gameState.updateAction(nextAction)
        case ProcessedGameActionResult.Backup(gameStateToReturnTo) =>
          gameStateToReturnTo
        case ProcessedGameActionResult.GameOver(result) =>
          gameState.copy(currentAction = None, result = Some(result))
    }
  }

  def handleActionResult[T](rootAction: GameAction[T], currentResult: PartialGameActionResult[T])(implicit gameState: GameState): (ProcessedGameActionResult[T], GameState) = {
    currentResult match {
      case PartialGameActionResult.Value(value) =>
        (ProcessedGameActionResult.Value(value), gameState)
      case PartialGameActionResult.Delegated(action) =>
        (ProcessedGameActionResult.Action(PartiallyExecutedActionWithChild(rootAction, action, (t: T, _) => t)), gameState)
      case PartialGameActionResult.ChildWithCallback(child, callback) =>
        (ProcessedGameActionResult.Action(PartiallyExecutedActionWithChild(rootAction, child, callback)), gameState)
      case PartialGameActionResult.Backup(gameStateToBackupTo) =>
        (ProcessedGameActionResult.Backup(gameStateToBackupTo), gameStateToBackupTo)
      case PartialGameActionResult.GameOver(result) =>
        (ProcessedGameActionResult.GameOver(result), gameState)
    }
  }

  def executeGameObjectAction[T](action: GameObjectAction[T], initialGameState: GameState): (ProcessedGameActionResult[T], GameState) = {
    executeGameObjectActions(Seq(action), initialGameState).mapLeft(_.asInstanceOf[ProcessedGameActionResult[T]])
  }

  @tailrec
  def executeGameObjectActions(actions: Seq[GameObjectAction[_]], initialGameState: GameState): (ProcessedGameActionResult[Unit], GameState) = {
    actions match {
      case action +: nextActions =>
        val preventResult = initialGameState.gameObjectState.activeContinuousEffects.ofType[PreventionEffect]
          .findOption(_.checkAction(action, initialGameState).asOptionalInstanceOf[Prevent])
        preventResult match {
          case Some(Prevent(logEvent)) =>
            (ProcessedGameActionResult.Value(()), initialGameState.recordLogEvent(logEvent))
          case None =>
            action match {
              case action: DirectGameObjectAction =>
                val gameObjectStateAfterAction = action.execute(initialGameState)
                val gameStateAfterAction = initialGameState.updateGameObjectState(gameObjectStateAfterAction)
                val triggeredAbilities = getTriggeringAbilities(action, gameStateAfterAction)
                val endedEffects = getEndedEffects(action, gameStateAfterAction)
                val finalGameObjectState = gameObjectStateAfterAction
                  .addWaitingTriggeredAbilities(triggeredAbilities)
                  .updateEffects(_.diff(endedEffects))
                val newGameState = initialGameState
                  .recordAction(action, ())
                  .updateGameObjectState(finalGameObjectState)
                  .recordLogEvent(action.getLogEvent(initialGameState))
                executeGameObjectActions(nextActions, newGameState)
              case action: DelegatingGameObjectAction =>
                executeGameObjectActions(action.delegate(initialGameState) ++ nextActions, initialGameState)
              case GameResultAction(gameResult) =>
                (ProcessedGameActionResult.GameOver(gameResult), initialGameState)
            }
        }
      case Nil =>
        (ProcessedGameActionResult.Value(()), initialGameState)
    }
  }

  private def getTriggeringAbilities(action: GameAction[_], gameStateAfterAction: GameState): Seq[TriggeredAbility] = {
    gameStateAfterAction.gameObjectState.activeTriggeredAbilities
      .filter(_.conditionMatchesEvent(action, gameStateAfterAction))
      .toSeq
  }

  private def getEndedEffects(action: GameObjectAction[_], gameStateAfterAction: GameState): Seq[FloatingActiveContinuousEffect] = {
    gameStateAfterAction.gameObjectState.floatingActiveContinuousEffects.filter(effect => {
      def matchesCondition = effect.matchesEndCondition(action, gameStateAfterAction)
      def objectIsGone = effect.effect.asOptionalInstanceOf[CharacteristicOrControlChangingContinuousEffect]
        .exists(e => !gameStateAfterAction.gameObjectState.allObjects.exists(_.objectId == e.affectedObject))
      matchesCondition || objectIsGone
    })
  }
}
