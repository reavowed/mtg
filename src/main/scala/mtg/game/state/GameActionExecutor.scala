package mtg.game.state

import mtg.abilities.TriggeredAbility
import mtg.effects.condition.EventCondition
import mtg.effects.continuous.{CharacteristicOrControlChangingContinuousEffect, PreventionEffect}
import mtg.effects.continuous.PreventionEffect.Result.Prevent
import mtg.game.PlayerId
import mtg.game.objects.FloatingActiveContinuousEffect

import scala.annotation.tailrec

object GameActionExecutor {

  def handleDecision(gameState: GameState, serializedDecision: String, actingPlayer: PlayerId): Option[GameState] = {
    runAction(gameState, handleDecision(_, serializedDecision, actingPlayer)(_))
      .map(executeAllActions)
  }

  def handleDecision[T](action: GameAction[T], serializedDecision: String, actingPlayer: PlayerId)(implicit gameState: GameState): Option[(NewGameActionResult.Terminal[T], GameState)] = {
    action match {
      case directChoice: DirectChoice[T] if (directChoice.playerToAct == actingPlayer) =>
        directChoice.handleDecision(serializedDecision).map(handleActionResult(directChoice, _))
      case WrappedChoice(child, furtherUpdates) if (child.playerToAct == actingPlayer) =>
        child.parseDecision(serializedDecision).map(d => executeOldUpdates(d.resultingActions ++ furtherUpdates, gameState).asInstanceOf[(NewGameActionResult.Terminal[T], GameState)])
      case PartiallyExecutedActionWithChild(rootAction, child, callback) =>
        handleDecisionForChild(rootAction, child, callback, serializedDecision, actingPlayer)
      case _ =>
        None
    }
  }

  private def handleDecisionForChild[T, S](
    rootAction: CompoundGameAction[T],
    childAction: GameAction[S],
    callback: (S, GameState) => NewGameActionResult.Partial[T],
    serializedDecision: String,
    actingPlayer: PlayerId)(
    implicit gameState: GameState
  ): Option[(NewGameActionResult.Terminal[T], GameState)] = {
    runChildAction[T, S](rootAction, childAction, callback, handleDecision[S](_, serializedDecision, actingPlayer)(_))
  }


  private def runAction(gameState: GameState, f: (GameAction[RootGameAction], GameState) => Option[(NewGameActionResult.Terminal[RootGameAction], GameState)]): Option[GameState] = {
    for {
      action <- gameState.currentAction
      (result, gameStateAfterAction) <- f(action, gameState)
    } yield updateGameState(gameStateAfterAction, result)
  }

  @tailrec
  def executeAllActions(gameState: GameState): GameState = {
    executeNextAction(gameState) match {
      case Some(newGameState) =>
        executeAllActions(newGameState)
      case None =>
        gameState
    }
  }

  def executeNextAction(gameState: GameState): Option[GameState] = {
    runAction(gameState, executeNextAction(_)(_))
  }

  def executeNextAction[T](gameAction: GameAction[T])(implicit gameState: GameState): Option[(NewGameActionResult.Terminal[T], GameState)] = gameAction match {
    case _: NewChoice[T] =>
      None
    case action: ExecutableGameAction[T] =>
      Some(handleActionResult(action, action.execute()))
    case PartiallyExecutedActionWithChild(rootAction, childAction, callback) =>
      executeChildAction(rootAction, childAction, callback)
    case WrappedOldUpdates(updates @ _*) =>
      Some(executeOldUpdates(updates, gameState).asInstanceOf[(NewGameActionResult.Terminal[T], GameState)])
  }

  private def executeChildAction[T, S](
    rootAction: CompoundGameAction[T],
    childAction: GameAction[S],
    callback: (S, GameState) => NewGameActionResult.Partial[T])(
    implicit gameState: GameState
  ): Option[(NewGameActionResult.Terminal[T], GameState)] = {
    runChildAction[T, S](rootAction, childAction, callback, executeNextAction[S](_)(_))
  }

  private def runChildAction[T, S](
    rootAction: CompoundGameAction[T],
    childAction: GameAction[S],
    callback: (S, GameState) => NewGameActionResult.Partial[T],
    f: (GameAction[S], GameState) => Option[(NewGameActionResult.Terminal[S], GameState)])(
    implicit gameState: GameState
  ): Option[(NewGameActionResult.Terminal[T], GameState)] = {
    f(childAction, gameState) map {
      case (NewGameActionResult.Value(s), newGameState) =>
        (NewGameActionResult.NewAction(PartiallyExecutedActionWithValue(rootAction, s, callback)), newGameState)
      case (NewGameActionResult.NewAction(action), newGameState) =>
        (NewGameActionResult.NewAction(PartiallyExecutedActionWithChild(rootAction, action, callback)), newGameState)
      case (halting: NewGameActionResult.Halting, newGameState) =>
        (halting, newGameState)
    }
  }

  private def updateGameState(gameState: GameState, newGameActionResult: NewGameActionResult.Terminal[RootGameAction]): GameState = {
    newGameActionResult match {
        case NewGameActionResult.Value(nextAction) =>
          gameState.copy(currentAction = Some(nextAction))
        case NewGameActionResult.NewAction(action) =>
          gameState.copy(currentAction = Some(action))
        case NewGameActionResult.Backup(gameStateToReturnTo) =>
          gameStateToReturnTo
        case NewGameActionResult.GameOver(result) =>
          gameState.copy(currentAction = None, result = Some(result))
    }
  }


  def handleActionResult[T](rootAction: CompoundGameAction[T], currentResult: NewGameActionResult.Partial[T])(implicit gameState: GameState): (NewGameActionResult.Terminal[T], GameState) = {
    currentResult match {
      case NewGameActionResult.Value(value) =>
        (NewGameActionResult.Value(value), gameState)
      case NewGameActionResult.Delegated(child: GameAction[_], callback) =>
        (NewGameActionResult.NewAction(PartiallyExecutedActionWithChild(rootAction, child, callback)), gameState)
      case terminal: NewGameActionResult.Halting =>
        (terminal, gameState)
    }
  }

  def executeOldUpdates(oldUpdates: Seq[OldGameUpdate], gameState: GameState): (NewGameActionResult.Terminal[Unit], GameState) = {
    oldUpdates match {
      case (head: InternalGameAction) +: tail =>
        val (newGameState, newUpdates) = execute(head, gameState)
        (NewGameActionResult.NewAction(WrappedOldUpdates(newUpdates ++ tail: _*)), newGameState)
      case (head: Choice) +: tail =>
        (NewGameActionResult.NewAction(WrappedChoice(head, tail)), gameState)
      case BackupAction(gameStateToRevertTo) +: _ =>
        (NewGameActionResult.Backup(gameStateToRevertTo), gameState)
      case Nil =>
        (NewGameActionResult.Value(()), gameState)
    }
  }

  def execute(action: InternalGameAction, initialGameState: GameState): (GameState, Seq[OldGameUpdate]) = {
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
        (newGameState, actionResult.nextUpdates)
    }
  }

  private def getTriggeringAbilities(action: InternalGameAction, gameStateAfterAction: GameState): Seq[TriggeredAbility] = {
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
