package mtg.game.turns

import mtg.game.PlayerIdentifier
import mtg.game.state.history.LogEvent
import mtg.game.state.{GameAction, GameState, InternalGameAction}

case class PriorityForPlayersAction(players: Seq[PlayerIdentifier]) extends InternalGameAction {
  override def execute(currentGameState: GameState): (Seq[GameAction], Option[LogEvent]) = {
    (PriorityChoice.create(players, currentGameState).toSeq, None)
  }
}
