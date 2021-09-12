package mtg.game.turns.turnEvents

import mtg.game.state.{GameState, InternalGameAction, InternalGameActionResult}
import mtg.game.turns.TurnPhase

case class BeginPhaseEvent(phase: TurnPhase) extends InternalGameAction {
  override def execute(currentGameState: GameState): InternalGameActionResult = {
    phase.actions :+ EndPhaseEvent(phase)
  }
}
