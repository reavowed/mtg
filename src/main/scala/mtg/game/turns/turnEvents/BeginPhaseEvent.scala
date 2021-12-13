package mtg.game.turns.turnEvents

import mtg.game.state.{GameActionResult, GameState, InternalGameAction}
import mtg.game.turns.TurnPhase

case class BeginPhaseEvent(phase: TurnPhase) extends InternalGameAction {
  override def execute(gameState: GameState): GameActionResult = {
    phase.actions :+ EndPhaseEvent(phase)
  }
  override def canBeReverted: Boolean = false
}
