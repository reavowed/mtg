package mtg.game.turns.turnEvents

import mtg.events.EmptyManaPoolsEvent
import mtg.game.state.history.LogEvent
import mtg.game.state.{GameAction, GameState, InternalGameAction}
import mtg.game.turns.TurnStep

case class EndStepEvent(step: TurnStep) extends InternalGameAction {
  override def execute(currentGameState: GameState): (Seq[GameAction], Option[LogEvent]) = {
    (Seq(EmptyManaPoolsEvent), None)
  }
}
