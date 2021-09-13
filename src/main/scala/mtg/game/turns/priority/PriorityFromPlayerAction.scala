package mtg.game.turns.priority

import mtg.game.PlayerId
import mtg.game.state.{GameState, InternalGameAction, InternalGameActionResult}

case class PriorityFromPlayerAction(player: PlayerId) extends InternalGameAction {
  override def execute(currentGameState: GameState): InternalGameActionResult = {
    PriorityForPlayersAction(currentGameState.gameData.getPlayersInApNapOrder(player))
  }
  override def canBeReverted: Boolean = true
}
