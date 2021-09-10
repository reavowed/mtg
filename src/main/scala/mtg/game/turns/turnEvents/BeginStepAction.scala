package mtg.game.turns.turnEvents

import mtg.game.state.{GameActionResult, GameState, TurnCycleAction, TurnState}
import mtg.game.turns.TurnStep

case class BeginStepAction(step: TurnStep) extends TurnCycleAction {
  override def execute(currentGameState: GameState): (TurnState, GameActionResult) = {
    (
      currentGameState.turnState.startStep(step),
      step.actions :+ EndStepAction(step)
    )
  }
}
