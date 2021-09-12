package mtg.game.turns.turnEvents

import mtg.game.state.{GameState, InternalGameAction, InternalGameActionResult}
import mtg.game.turns.TurnStep

case class BeginStepEvent(step: TurnStep) extends InternalGameAction {
  override def execute(currentGameState: GameState): InternalGameActionResult = {
    step.actions :+ EndStepEvent(step)
  }
  override def canBeReverted: Boolean = false
}
