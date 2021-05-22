package mtg.game.turns

import mtg.game.PlayerIdentifier
import mtg.game.state.history.LogEvent
import mtg.game.state.{GameAction, GameState, InternalGameAction}

case class PriorityFromPlayerAction(player: PlayerIdentifier) extends InternalGameAction {
  override def execute(currentGameState: GameState): (Seq[GameAction], Option[LogEvent]) = {
    (PriorityChoice.create(currentGameState.gameData.getPlayersInApNapOrder(player), currentGameState).toSeq, None)
  }
}
