package mtg.game.turns.turnEvents

import mtg.game.state.history.{GameHistory, LogEvent}
import mtg.game.state.{GameAction, GameState, TurnCycleEvent}
import mtg.game.turns.TurnStep

case class BeginStepEvent(step: TurnStep) extends TurnCycleEvent {
  override def execute(currentGameState: GameState): (GameHistory => GameHistory, Seq[GameAction], Option[LogEvent]) = {
    (
      _.startStep(step),
      step.actions :+ EndStepEvent(step),
      None
    )
  }
}
