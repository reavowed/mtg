package mtg.game.turns

import mtg.events.EmptyManaPoolsEvent
import mtg.game.state.history.LogEvent
import mtg.game.state.{GameAction, GameState, InternalGameAction}

case class EndPhaseEvent(phase: TurnPhase) extends InternalGameAction {
  override def execute(currentGameState: GameState): (Seq[GameAction], Option[LogEvent]) = {
    (Seq(EmptyManaPoolsEvent), None)
  }
}
