package mtg.game.turns

import mtg.game.state.history.LogEvent
import mtg.game.state.{GameAction, GameState, InternalGameAction}

case object PriorityAction extends InternalGameAction {
  override def execute(currentGameState: GameState): (Seq[GameAction], Option[LogEvent]) = {
    (Seq(PriorityChoice(currentGameState.playersInApnapOrder)), None)
  }
}
