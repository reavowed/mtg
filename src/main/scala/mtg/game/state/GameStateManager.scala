package mtg.game.state

import mtg.game.turns.TurnCycleEventPreventer
import mtg.game.turns.TurnPhase.{PostcombatMainPhase, PrecombatMainPhase}
import mtg.game.turns.priority.PriorityChoice
import mtg.game.{GameStartingData, PlayerIdentifier}

import scala.annotation.tailrec
import scala.collection.mutable

class GameStateManager(private var _currentGameState: GameState, val onStateUpdate: GameState => Unit, val stops: mutable.Map[PlayerIdentifier, Map[PlayerIdentifier, Seq[AnyRef]]]) {
  def currentGameState: GameState = this.synchronized { _currentGameState }

  executeAutomaticActions()

  private def executeAutomaticActions(): Unit = {
    executeAutomaticActions(currentGameState)
  }

  @tailrec
  private def executeAutomaticActions(gameState: GameState): Unit = {
    gameState.popAction() match {
      case (turnCycleEvent: TurnCycleEvent, gameState) =>
        executeAutomaticActions(executeTurnCycleEvent(turnCycleEvent, gameState))
      case (internalGameAction: InternalGameAction, gameState) =>
        executeAutomaticActions(executeInternalGameAction(internalGameAction, gameState))
      case (gameObjectEvent: GameObjectEvent, gameState) =>
        executeAutomaticActions(executeGameObjectEvent(gameObjectEvent, gameState))
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

  private def executeTurnCycleEvent(turnCycleEvent: TurnCycleEvent, gameState: GameState): GameState = {
    val preventResult = TurnCycleEventPreventer.fromRules.collectFirst(Function.unlift(_.checkEvent(turnCycleEvent, gameState).asOptionalInstanceOf[TurnCycleEventPreventer.Result.Prevent]))
    preventResult match {
      case Some(TurnCycleEventPreventer.Result.Prevent(logEvent)) =>
        logEvent.map(gameState.recordLogEvent).getOrElse(gameState)
      case _ =>
        val (historyUpdater, actions, logEvent) = turnCycleEvent.execute(gameState)
        gameState.updateHistory(historyUpdater).addActions(actions).recordLogEvent(logEvent)
    }
  }

  private def executeInternalGameAction(internalGameAction: InternalGameAction, gameState: GameState): GameState = {
    val InternalGameActionResult(actions, logEvent) = internalGameAction.execute(gameState)
    gameState.addActions(actions).recordLogEvent(logEvent)
  }

  private def executeGameObjectEvent(gameObjectEvent: GameObjectEvent, gameState: GameState): GameState = {
    gameObjectEvent.execute(gameState)
      .updateGameState(gameState)
      .recordGameEvent(gameObjectEvent)
  }

  def handleDecision(serializedDecision: String, actingPlayer: PlayerIdentifier): Unit = this.synchronized {
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
      case Some((decision, actions, logEvent)) =>
        Some(gameState.recordGameEvent(decision).addActions(actions).recordLogEvent(logEvent))
      case None =>
        None
    }
  }
}

object GameStateManager {
  def initial(gameStartingData: GameStartingData, onStateUpdate: GameState => Unit): GameStateManager = {
    val initialStops = mutable.Map(gameStartingData.players.map(p =>
      p -> gameStartingData.players.map[(PlayerIdentifier, Seq[AnyRef])](q => q -> (if (p == q) Seq(PrecombatMainPhase, PostcombatMainPhase) else Nil)).toMap): _*)
    new GameStateManager(GameState.initial(gameStartingData), onStateUpdate, initialStops)
  }
}
