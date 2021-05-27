package mtg.game.turns.priority

import mtg.game.PlayerIdentifier
import mtg.game.state.history.LogEvent
import mtg.game.state.{GameAction, GameState, InternalGameAction, InternalGameActionResult}

case class PriorityFromPlayerAction(player: PlayerIdentifier) extends InternalGameAction {
  override def execute(currentGameState: GameState): InternalGameActionResult = {
    PriorityForPlayersAction(currentGameState.gameData.getPlayersInApNapOrder(player)).execute(currentGameState)
  }
}
