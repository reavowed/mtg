package mtg.game.turns.priority

import mtg.game.PlayerId
import mtg.game.state.{GameState, InternalGameAction, GameActionResult}

case class PriorityFromPlayerAction(player: PlayerId) extends InternalGameAction {
  override def execute(gameState: GameState): GameActionResult = {
    PriorityForPlayersAction(gameState.gameData.getPlayersInApNapOrder(player))
  }
  override def canBeReverted: Boolean = true
}
