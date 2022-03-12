package mtg.game.state

import mtg.abilities.TriggeredAbility
import mtg.continuousEffects.PreventionEffect.Result.Prevent
import mtg.continuousEffects.{CharacteristicOrControlChangingContinuousEffect, FloatingActiveContinuousEffect, PreventionEffect}
import mtg.core.PlayerId
import mtg.effects.condition.EventCondition
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
    runAction(gameState, handleDecision(_, serializedDecision, actingPlayer)(_))
      .map(executeAllActions)
  }

  def handleDecision[T](action: GameAction[T], serializedDecision: String, actingPlayer: PlayerId)(implicit gameState: GameState): Option[(ProcessedGameActionResult[T], GameState)] = {
    action match {
      case directChoice: Choice[T] if directChoice.playerToAct == actingPlayer =>
        directChoice.handleDecision(serializedDecision).map(d => (ProcessedGameActionResult.Value(d), gameState.recordChoice(directChoice, d)))
      case PartiallyExecutedActionWithChild(rootAction, child, callback) =>
        handleDecisionForChild(rootAction, child, callback, serializedDecision, actingPlayer)
      case _ =>
        None
    }
  }

  private def handleDecisionForChild[T, S](
    rootAction: GameAction[T],
    childAction: GameAction[S],
    callback: (S, GameState) => PartialGameActionResult[T],
    serializedDecision: String,
    actingPlayer: PlayerId)(
    implicit gameState: GameState
  ): Option[(ProcessedGameActionResult[T], GameState)] = {
    runChildAction[T, S](rootAction, childAction, callback, handleDecision[S](_, serializedDecision, actingPlayer)(_))
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
    runAction(gameState, executeNextAction(_)(_, stops))
  }

  def executeNextAction[T](gameAction: GameAction[T])(implicit gameState: GameState, stops: Stops): Option[(ProcessedGameActionResult[T], GameState)] = gameAction match {
    case priorityChoice: PriorityChoice if stops.shouldAutoPass(priorityChoice, gameState) =>
      handleDecision(priorityChoice, "Pass", priorityChoice.playerToAct)
    case _: Choice[T] =>
      None
    case action: ExecutableGameAction[T] =>
      Some(executeAction(action))
    case PartiallyExecutedActionWithValue(rootAction, value, callback) =>
      Some(executeCallback(rootAction, value, callback))
    case PartiallyExecutedActionWithChild(rootAction, childAction, callback) =>
      executeChildAction(rootAction, childAction, callback)
    case WrappedOldUpdates(updates @ _*) =>
      Some(executeOldActions(updates, gameState).asInstanceOf[(ProcessedGameActionResult[T], GameState)])
    case LogEventAction(logEvent) =>
      Some((ProcessedGameActionResult.Value(()).asInstanceOf[ProcessedGameActionResult[T]], gameState.recordLogEvent(logEvent)))
  }

  private def executeAction[T](action: ExecutableGameAction[T])(implicit gameState: GameState):  (ProcessedGameActionResult[T], GameState) = {
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
    runChildAction[T, S](rootAction, childAction, callback, executeNextAction[S](_)(_, stops))
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
      case PartialGameActionResult.ChildWithCallback(child, callback) =>
        (ProcessedGameActionResult.Action(PartiallyExecutedActionWithChild(rootAction, child, callback)), gameState)
      case PartialGameActionResult.Backup(gameStateToBackupTo) =>
        (ProcessedGameActionResult.Backup(gameStateToBackupTo), gameStateToBackupTo)
      case PartialGameActionResult.GameOver(result) =>
        (ProcessedGameActionResult.GameOver(result), gameState)
    }
  }

  def executeOldActions(oldActions: Seq[InternalGameAction], gameState: GameState): (ProcessedGameActionResult[Unit], GameState) = {
    oldActions match {
      case head +: tail =>
        val (newGameState, newUpdates) = execute(head, gameState)
        (ProcessedGameActionResult.Action(WrappedOldUpdates(newUpdates ++ tail: _*)), newGameState)
      case Nil =>
        (ProcessedGameActionResult.Value(()), gameState)
    }
  }

  def execute(action: InternalGameAction, initialGameState: GameState): (GameState, Seq[InternalGameAction]) = {
    val preventResult = initialGameState.gameObjectState.activeContinuousEffects.ofType[PreventionEffect]
      .findOption(_.checkAction(action, initialGameState).asOptionalInstanceOf[Prevent])

    preventResult match {
      case Some(Prevent(logEvent)) =>
        (logEvent.map(initialGameState.recordLogEvent).getOrElse(initialGameState), Nil)
      case None =>
        val actionResult = action.execute(initialGameState)
        val gameObjectStateAfterAction = actionResult.newGameObjectState.getOrElse(initialGameState.gameObjectState)
        val gameStateAfterAction = initialGameState.updateGameObjectState(gameObjectStateAfterAction)
        val triggeredAbilities = getTriggeringAbilities(action, gameStateAfterAction)
        val endedEffects = getEndedEffects(action, gameStateAfterAction)

        val finalGameObjectState = if (actionResult.newGameObjectState.isDefined || triggeredAbilities.nonEmpty || endedEffects.nonEmpty) {
          gameObjectStateAfterAction
            .addWaitingTriggeredAbilities(triggeredAbilities)
            .updateEffects(_.filter(!endedEffects.contains(_)))
        } else {
          initialGameState.gameObjectState
        }

        val newGameState = initialGameState
          .recordAction(action)
          .updateGameObjectState(finalGameObjectState)
          .recordLogEvent(actionResult.logEvent)
        (newGameState, actionResult.nextActions)
    }
  }

  private def getTriggeringAbilities(action: GameUpdate, gameStateAfterAction: GameState): Seq[TriggeredAbility] = {
    gameStateAfterAction.gameObjectState.activeTriggeredAbilities.filter {
      _.getCondition(gameStateAfterAction) match {
        case eventCondition: EventCondition =>
          eventCondition.matchesEvent(action, gameStateAfterAction)
      }
    }.toSeq
  }

  private def getEndedEffects(action: InternalGameAction, gameStateAfterAction: GameState): Seq[FloatingActiveContinuousEffect] = {
    gameStateAfterAction.gameObjectState.floatingActiveContinuousEffects.filter(effect => {
      def matchesCondition = effect.endCondition match {
        case eventCondition: EventCondition =>
          eventCondition.matchesEvent(action, gameStateAfterAction)
      }
      def objectIsGone = effect.effect.asOptionalInstanceOf[CharacteristicOrControlChangingContinuousEffect]
        .exists(e => !gameStateAfterAction.gameObjectState.allObjects.exists(_.objectId == e.affectedObject))
      matchesCondition || objectIsGone
    })
  }
}
