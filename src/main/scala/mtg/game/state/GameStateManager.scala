package mtg.game.state

import mtg.abilities.TriggeredAbility
import mtg.effects.condition.EventCondition
import mtg.effects.continuous.{CharacteristicOrControlChangingContinuousEffect, PreventionEffect}
import mtg.effects.continuous.PreventionEffect.Result.Prevent
import mtg.game.objects.FloatingActiveContinuousEffect
import mtg.game.turns.TurnPhase.{PostcombatMainPhase, PrecombatMainPhase}
import mtg.game.turns.priority.PriorityChoice
import mtg.game.{GameStartingData, PlayerId}

import scala.annotation.tailrec
import scala.collection.mutable

class GameStateManager(private var _currentGameState: GameState, val onStateUpdate: GameState => Unit, val stops: mutable.Map[PlayerId, Map[PlayerId, Seq[AnyRef]]]) {
  def gameState: GameState = this.synchronized { _currentGameState }

  private def updateState(newState: GameState): Unit = {
      _currentGameState = newState
      onStateUpdate(_currentGameState)
  }

  executeAutomaticActions()

  private def executeAutomaticActions(): Unit = {
    executeAutomaticActions(gameState)
  }

  @tailrec
  private def executeAutomaticActions(gameState: GameState): Unit = {
    gameState.popUpdate() match {
      case (gameEvent: InternalGameAction, gameState) =>
        executeAutomaticActions(executeAutomaticAction(gameEvent, gameState))
      case (BackupAction(gameStateToRevertTo), _) =>
        executeAutomaticActions(gameStateToRevertTo)
      case (priorityChoice: PriorityChoice, gameState)
        if !stops(priorityChoice.playerToAct)(gameState.activePlayer).exists(gameState.currentStep.orElse(gameState.currentPhase).contains)
      =>
        executeAutomaticActions(executeDecision(priorityChoice, "Pass", gameState).get)
      case _ =>
        updateState(gameState)
    }
  }

  private def executeAutomaticAction(action: InternalGameAction, initialGameState: GameState): GameState = {
    val preventResult = initialGameState.gameObjectState.activeContinuousEffects.ofType[PreventionEffect]
      .findOption(_.checkAction(action, initialGameState).asOptionalInstanceOf[Prevent])

    preventResult match {
      case Some(Prevent(logEvent)) =>
        logEvent.map(initialGameState.recordLogEvent).getOrElse(initialGameState)
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

        initialGameState
          .recordAction(action)
          .updateGameObjectState(finalGameObjectState)
          .addUpdates(actionResult.nextUpdates)
          .recordLogEvent(actionResult.logEvent)
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

  def handleDecision(serializedDecision: String, actingPlayer: PlayerId): Unit = this.synchronized {
    gameState.popUpdate() match {
      case (choice: Choice, gameState) if choice.playerToAct == actingPlayer =>
        executeDecision(choice, serializedDecision, gameState) match {
          case Some(gameState) =>
            executeAutomaticActions(gameState)
          case None =>
        }
      case _ =>
    }
  }

  def executeDecision(choice: Choice, serializedDecision: String, gameState: GameState): Option[GameState] = {
    choice.parseDecision(serializedDecision) match {
      case Some(decision) =>
        Some(gameState.recordChoice(choice).addUpdates(decision.resultingActions))
      case None =>
        None
    }
  }

  def requestUndo(playerId: PlayerId): Unit = {
    UndoHelper.requestUndo(playerId, gameState).foreach(updateState)
  }
}

object GameStateManager {
  def initial(gameStartingData: GameStartingData, onStateUpdate: GameState => Unit): GameStateManager = {
    val initialStops = mutable.Map(gameStartingData.players.map(p =>
      p -> gameStartingData.players.map[(PlayerId, Seq[AnyRef])](q => q -> (if (p == q) Seq(PrecombatMainPhase, PostcombatMainPhase) else Nil)).toMap): _*)
    new GameStateManager(GameState.initial(gameStartingData), onStateUpdate, initialStops)
  }
}
