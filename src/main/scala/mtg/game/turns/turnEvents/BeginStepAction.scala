package mtg.game.turns.turnEvents

import mtg.game.state.history.GameEvent
import mtg.game.state.{InternalGameActionResult, GameState, TurnCycleAction}
import mtg.game.turns.TurnStep

case class BeginStepAction(step: TurnStep) extends TurnCycleAction {
  override def execute(currentGameState: GameState): InternalGameActionResult = {
    (step.actions :+ EndStepAction(step), BeginStepEvent(step))
  }
}

case class BeginStepEvent(step: TurnStep) extends GameEvent
