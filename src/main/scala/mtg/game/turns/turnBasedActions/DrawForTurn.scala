package mtg.game.turns.turnBasedActions

import mtg.events.DrawCardEvent
import mtg.game.state.history.LogEvent
import mtg.game.state.{GameAction, GameState, InternalGameAction}

case object DrawForTurn extends InternalGameAction {
  override def execute(currentGameState: GameState): (Seq[GameAction], Option[LogEvent]) = {
    (Seq(DrawCardEvent(currentGameState.activePlayer)), Some(LogEvent.DrawForTurn(currentGameState.activePlayer)))
  }
}
