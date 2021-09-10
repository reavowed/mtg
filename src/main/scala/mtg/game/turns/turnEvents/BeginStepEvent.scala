package mtg.game.turns.turnEvents

import mtg.game.state.{GameActionResult, GameState, TurnCycleEvent, TurnState}
import mtg.game.turns.TurnStep

case class BeginStepEvent(step: TurnStep) extends TurnCycleEvent {
  override def execute(currentGameState: GameState): (TurnState, GameActionResult) = {
    (
      currentGameState.turnState.startStep(step),
      step.actions :+ EndStepEvent(step)
    )
  }
}
