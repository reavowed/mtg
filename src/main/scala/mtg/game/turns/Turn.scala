package mtg.game.turns

import mtg.core.PlayerId
import mtg.game.state.GameState

case class Turn(number: Int, activePlayer: PlayerId) {
  def next(gameState: GameState): Turn = Turn(number + 1, gameState.gameData.getNextPlayerInTurnOrder(activePlayer))
}
