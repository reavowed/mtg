package mtg.game.turns.turnEvents

import mtg.game.state.history.GameEvent
import mtg.game.state.{GameState, InternalGameAction, InternalGameActionResult}
import mtg.game.turns.TurnStep

case class BeginStepAction(step: TurnStep) extends InternalGameAction {
  override def execute(currentGameState: GameState): InternalGameActionResult = {
    (step.actions :+ EndStepAction(step), BeginStepEvent(step))
  }
}

case class BeginStepEvent(step: TurnStep) extends GameEvent
