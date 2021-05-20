package mtg.game.actions

import mtg.game.PlayerIdentifier
import mtg.game.state.GameState
import mtg.game.turns.MainPhase

object TimingChecks {
  def isPlayersTurn(player: PlayerIdentifier, gameState: GameState): Boolean = {
    player == gameState.activePlayer
  }
  def isMainPhase(gameState: GameState): Boolean = {
    gameState.currentPhase.exists(_.isInstanceOf[MainPhase])
  }

  def isMainPhaseOfPlayersTurnWithEmptyStack(player: PlayerIdentifier, gameState: GameState): Boolean = {
    // TODO: check stack is empty
    isPlayersTurn(player, gameState) && isMainPhase(gameState)
  }

}
