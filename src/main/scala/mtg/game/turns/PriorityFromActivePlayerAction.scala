package mtg.game.turns

import mtg.game.state.history.LogEvent
import mtg.game.state.{GameAction, GameState, InternalGameAction}

case object PriorityFromActivePlayerAction extends InternalGameAction {
  override def execute(currentGameState: GameState): (Seq[GameAction], Option[LogEvent]) = {
    (Seq(PriorityFromPlayerAction(currentGameState.activePlayer)), None)
  }
}
