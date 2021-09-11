package mtg.game.turns.turnEvents

import mtg.game.state.history.{GameHistory, LogEvent}
import mtg.game.state.{GameAction, GameState, InternalGameActionResult, TurnCycleEvent}
import mtg.game.turns.TurnStep

case class BeginStepEvent(step: TurnStep) extends TurnCycleEvent {
  override def execute(currentGameState: GameState): (GameHistory => GameHistory, InternalGameActionResult) = {
    (
      _.startStep(step),
      step.actions :+ EndStepEvent(step)
    )
  }
}
