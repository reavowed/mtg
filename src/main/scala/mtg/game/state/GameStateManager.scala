package mtg.game.state

import mtg.core.PlayerId
import mtg.game.GameStartingData

class GameStateManager(private var _currentGameState: GameState, val onStateUpdate: GameState => Unit, var stops: Stops) {
  def gameState: GameState = this.synchronized { _currentGameState }

  private def updateState(newState: GameState): Unit = {
    _currentGameState = newState
    onStateUpdate(_currentGameState)
  }

  executeAutomaticActionsAndUpdate()

  private def executeAutomaticActionsAndUpdate(): Unit = {
    updateState(GameActionExecutor.executeAll(gameState)(stops))
  }

  def handleDecision(serializedDecision: String, actingPlayer: PlayerId): Unit = this.synchronized {
    GameActionExecutor.handleDecision(gameState, serializedDecision, actingPlayer)(stops).foreach(updateState)
  }

  def requestUndo(playerId: PlayerId): Unit = {
    UndoHelper.requestUndo(playerId, gameState).foreach(updateState)
  }

  def setStop(playerWithStop: PlayerId, activePlayer: PlayerId, stepOrPhase: AnyRef): Unit = {
    stops = stops.set(playerWithStop, activePlayer, stepOrPhase)
  }

  def unsetStop(playerWithStop: PlayerId, activePlayer: PlayerId, stepOrPhase: AnyRef): Unit = {
    stops = stops.unset(playerWithStop, activePlayer, stepOrPhase)
  }
}

object GameStateManager {
  def initial(gameStartingData: GameStartingData, onStateUpdate: GameState => Unit): GameStateManager = {
    new GameStateManager(GameState.initial(gameStartingData), onStateUpdate, Stops.default(gameStartingData))
  }
}
