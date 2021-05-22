package mtg.game.turns.priority

import mtg.game.PlayerIdentifier
import mtg.game.state.history.LogEvent
import mtg.game.state.{GameAction, GameState, InternalGameAction}

case class PriorityFromPlayerAction(player: PlayerIdentifier) extends InternalGameAction {
  override def execute(currentGameState: GameState): (Seq[GameAction], Option[LogEvent]) = {
    PriorityForPlayersAction(currentGameState.gameData.getPlayersInApNapOrder(player)).execute(currentGameState)
  }
}
