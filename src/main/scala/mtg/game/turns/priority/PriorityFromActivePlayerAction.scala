package mtg.game.turns.priority

import mtg.game.state.{GameState, InternalGameAction, InternalGameActionResult}

case object PriorityFromActivePlayerAction extends InternalGameAction {
  override def execute(currentGameState: GameState): InternalGameActionResult = {
    PriorityForPlayersAction(currentGameState.gameData.getPlayersInApNapOrder(currentGameState.activePlayer))
  }
  override def canBeReverted: Boolean = true
}
