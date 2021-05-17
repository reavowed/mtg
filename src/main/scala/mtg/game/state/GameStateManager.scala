package mtg.game.state

import mtg.game.state.GameEvent.ResolvedEvent
import mtg.game.{GameStartingData, PlayerIdentifier}

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
      case (gameActionManager: GameActionManager, gameState) =>
        executeAutomaticActions(executeGameActionManager(gameActionManager, gameState))
      case (gameObjectEvent: GameObjectEvent, gameState) =>
        executeAutomaticActions(executeGameObjectEvent(gameObjectEvent, gameState))
      case _ =>
        _currentGameState = gameState
        onStateUpdate(_currentGameState)
    }
  }

  private def executeGameActionManager(gameActionManager: GameActionManager, gameState: GameState): GameState = {
    val (actions, logEvent) = gameActionManager.execute(gameState)
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
