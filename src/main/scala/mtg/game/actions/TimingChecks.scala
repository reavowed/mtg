package mtg.game.actions

import mtg.game.PlayerId
import mtg.game.state.GameState
import mtg.game.turns.MainPhase

object TimingChecks {
  def isPlayersTurn(player: PlayerId, gameState: GameState): Boolean = {
    player == gameState.activePlayer
  }
  def isMainPhase(gameState: GameState): Boolean = {
    gameState.turnState.currentPhase.exists(_.isInstanceOf[MainPhase])
  }
  def isStackEmpty(gameState: GameState): Boolean = {
    gameState.gameObjectState.stack.isEmpty
  }

  def isMainPhaseOfPlayersTurnWithEmptyStack(player: PlayerId, gameState: GameState): Boolean = {
    isPlayersTurn(player, gameState) && isMainPhase(gameState) && isStackEmpty(gameState)
  }
}
