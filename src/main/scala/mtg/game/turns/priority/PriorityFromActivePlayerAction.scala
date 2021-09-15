package mtg.game.turns.priority

import mtg.game.state.{GameState, InternalGameAction, GameActionResult}

case object PriorityFromActivePlayerAction extends InternalGameAction {
  override def execute(gameState: GameState): GameActionResult = {
    PriorityForPlayersAction(gameState.gameData.getPlayersInApNapOrder(gameState.activePlayer))
  }
  override def canBeReverted: Boolean = true
}
