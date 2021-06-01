package mtg.game.turns.priority

import mtg.game.PlayerId
import mtg.game.state.history.LogEvent
import mtg.game.state.{GameAction, GameState, InternalGameAction, GameActionResult}

case class PriorityFromPlayerAction(player: PlayerId) extends InternalGameAction {
  override def execute(currentGameState: GameState): GameActionResult = {
    PriorityForPlayersAction(currentGameState.gameData.getPlayersInApNapOrder(player)).execute(currentGameState)
  }
}
