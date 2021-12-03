package mtg.game.state

import mtg.game.turns.TurnPhase.{PostcombatMainPhase, PrecombatMainPhase}
import mtg.game.{GameStartingData, PlayerId}

import scala.collection.mutable

class GameStateManager(private var _currentGameState: GameState, val onStateUpdate: GameState => Unit, val stops: mutable.Map[PlayerId, Map[PlayerId, Seq[AnyRef]]]) {
  def gameState: GameState = this.synchronized { _currentGameState }

  private def updateState(newState: GameState): Unit = {
    _currentGameState = newState
    onStateUpdate(_currentGameState)
  }

  executeAutomaticActionsAndUpdate()

  private def executeAutomaticActionsAndUpdate(): Unit = {
    updateState(GameActionExecutor.executeAllActions(gameState))
  }

  def handleDecision(serializedDecision: String, actingPlayer: PlayerId): Unit = this.synchronized {
    GameActionExecutor.handleDecision(gameState, serializedDecision, actingPlayer).foreach(updateState)
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
