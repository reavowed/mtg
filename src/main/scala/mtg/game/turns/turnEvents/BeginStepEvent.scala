package mtg.game.turns.turnEvents

import mtg.game.state.{GameActionResult, GameState, InternalGameAction}
import mtg.game.turns.TurnStep

case class BeginStepEvent(step: TurnStep) extends InternalGameAction {
  override def execute(gameState: GameState): GameActionResult = {
    step.actions :+ EndStepEvent(step)
  }
  override def canBeReverted: Boolean = false
}
