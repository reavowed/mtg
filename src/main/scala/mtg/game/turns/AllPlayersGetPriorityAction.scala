package mtg.game.turns

import mtg.game.state.history.LogEvent
import mtg.game.state.{GameAction, GameState, InternalGameAction}

case object AllPlayersGetPriorityAction extends InternalGameAction {
  override def execute(currentGameState: GameState): (Seq[GameAction], Option[LogEvent]) = {
    (PriorityChoice.create(currentGameState.playersInApnapOrder, currentGameState).toSeq, None)
  }
}
