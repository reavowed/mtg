package mtg.game.state

import mtg.game.state.history.GameEvent.ResolvedEvent
import mtg.game.turns.TurnCycleEventPreventer
import mtg.game.{GameStartingData, PlayerIdentifier, turns}

import scala.annotation.tailrec

class GameStateManager(private var _currentGameState: GameState, val onStateUpdate: GameState => Unit) {
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
    val (actions, logEvent) = internalGameAction.execute(gameState)
    gameState.addActions(actions).recordLogEvent(logEvent)
  }

  private def executeGameObjectEvent(gameObjectEvent: GameObjectEvent, gameState: GameState): GameState = {
    gameObjectEvent.execute(gameState)
      .updateGameState(gameState)
      .recordGameEvent(ResolvedEvent(gameObjectEvent))
  }

  def handleDecision(serializedDecision: String, actingPlayer: PlayerIdentifier): Unit = this.synchronized {
    currentGameState.popAction() match {
      case (choice: Choice, gameState) if choice.playerToAct == actingPlayer =>
        choice.handleDecision(serializedDecision, gameState) match {
          case Some((decision, actions, logEvent)) =>
            executeAutomaticActions(gameState.recordGameEvent(decision).addActions(actions).recordLogEvent(logEvent))
          case None =>
        }
      case _ =>
    }
  }
}

object GameStateManager {
  def initial(gameStartingData: GameStartingData, onStateUpdate: GameState => Unit): GameStateManager = {
    new GameStateManager(GameState.initial(gameStartingData), onStateUpdate)
  }
}
