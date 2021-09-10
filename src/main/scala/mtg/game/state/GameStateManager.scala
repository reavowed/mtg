package mtg.game.state

import mtg.abilities.TriggeredAbility
import mtg.effects.condition.EventCondition
import mtg.game.objects.FloatingActiveContinuousEffect
import mtg.game.state.history.GameEvent
import mtg.game.turns.GameActionPreventionEffect
import mtg.game.turns.TurnPhase.{PostcombatMainPhase, PrecombatMainPhase}
import mtg.game.turns.priority.PriorityChoice
import mtg.game.{GameStartingData, PlayerId}

import scala.annotation.tailrec
import scala.collection.mutable

class GameStateManager(private var _currentGameState: GameState, val onStateUpdate: GameState => Unit, val stops: mutable.Map[PlayerId, Map[PlayerId, Seq[AnyRef]]]) {
  def currentGameState: GameState = this.synchronized { _currentGameState }

  executeAutomaticActions()

  private def executeAutomaticActions(): Unit = {
    executeAutomaticActions(currentGameState)
  }

  @tailrec
  private def executeAutomaticActions(gameState: GameState): Unit = {
    gameState.popAction() match {
      case (automaticGameAction: AutomaticGameAction, gameState) =>
        executeAutomaticActions(executeAutomaticGameAction(automaticGameAction, gameState))
      case (BackupAction(gameStateToRevertTo), _) =>
        executeAutomaticActions(gameStateToRevertTo)
      case (priorityChoice: PriorityChoice, gameState)
        if !stops(priorityChoice.playerToAct)(gameState.activePlayer).exists(gameState.currentStep.orElse(gameState.currentPhase).contains)
      =>
        executeDecision(priorityChoice, "Pass", gameState) match {
          case Some(gameState) =>
            executeAutomaticActions(gameState)
          case None =>
        }
      case _ =>
        _currentGameState = gameState
        onStateUpdate(_currentGameState)
    }
  }

  private def executeAutomaticGameAction(gameAction: AutomaticGameAction, gameState: GameState): GameState = {
    val preventResult = gameState.gameObjectState.activePreventionEffects.mapFind(_.checkEvent(gameAction, gameState).asOptionalInstanceOf[GameActionPreventionEffect.Result.Prevent])
    preventResult match {
      case Some(GameActionPreventionEffect.Result.Prevent(logEvent)) =>
        logEvent.map(gameState.recordLogEvent).getOrElse(gameState)
      case _ =>
        val actionResult = gameAction.execute(gameState)
        handleGameActionResult(actionResult, gameState)
    }
  }

  private def handleGameActionResult(gameActionResult: GameActionResult, initialGameState: GameState): GameState = {
    val stateAfterObjectUpdate = initialGameState.updateGameObjectState(gameActionResult.newGameObjectState)
    handleGameEvent(gameActionResult.gameEvent, stateAfterObjectUpdate)
      .addActions(gameActionResult.childActions)
      .recordLogEvent(gameActionResult.logEvent)
  }

  private def handleGameEvent(gameEventOption: Option[GameEvent], finalGameState: GameState): GameState = {
    gameEventOption.map(gameEvent => {
      val triggeredAbilities = getTriggeredAbilities(gameEvent, finalGameState)
      finalGameState.updateGameObjectState(
        _.addWaitingTriggeredAbilities(triggeredAbilities).updateEffects(removeEffectsWithNoReferent(gameEvent, finalGameState, _))
      ).recordGameEvent(gameEvent)
    }).getOrElse(finalGameState)
  }

  private def getTriggeredAbilities(gameEvent: GameEvent, finalGameState: GameState): Seq[TriggeredAbility] = {
    finalGameState.gameObjectState.activeTriggeredAbilities.filter {
      _.getCondition(finalGameState) match {
        case eventCondition: EventCondition =>
          eventCondition.matchesEvent(gameEvent, finalGameState)
      }
    }.toSeq
  }
  private def removeEffectsWithNoReferent(gameEvent: GameEvent, gameState: GameState, effects: Seq[FloatingActiveContinuousEffect]): Seq[FloatingActiveContinuousEffect] = {
    effects.filter(activeEffect => {
      val objectExists = gameState.gameObjectState.allObjects.exists(_.objectId == activeEffect.effect.affectedObject)
      val matchesCondition = activeEffect.endCondition match {
        case eventCondition: EventCondition =>
          eventCondition.matchesEvent(gameEvent, gameState)
      }
      objectExists && !matchesCondition
    })
  }

  def handleDecision(serializedDecision: String, actingPlayer: PlayerId): Unit = this.synchronized {
    currentGameState.popAction() match {
      case (choice: PlayerChoice, gameState) if choice.playerToAct == actingPlayer =>
        executeDecision(choice, serializedDecision, gameState) match {
          case Some(gameState) =>
            executeAutomaticActions(gameState)
          case None =>
        }
      case _ =>
    }
  }

  def executeDecision(choice: PlayerChoice, serializedDecision: String, gameState: GameState): Option[GameState] = {
    choice.handleDecision(serializedDecision, gameState) match {
      case Some((decision, actionResult)) =>
        Some(handleGameActionResult(actionResult, gameState.recordGameEvent(decision)))
      case None =>
        None
    }
  }
}

object GameStateManager {
  def initial(gameStartingData: GameStartingData, onStateUpdate: GameState => Unit): GameStateManager = {
    val initialStops = mutable.Map(gameStartingData.players.map(p =>
      p -> gameStartingData.players.map[(PlayerId, Seq[AnyRef])](q => q -> (if (p == q) Seq(PrecombatMainPhase, PostcombatMainPhase) else Nil)).toMap): _*)
    new GameStateManager(GameState.initial(gameStartingData), onStateUpdate, initialStops)
  }
}
