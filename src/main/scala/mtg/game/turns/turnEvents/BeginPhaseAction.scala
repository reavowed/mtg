package mtg.game.turns.turnEvents

import mtg.game.state.history.GameEvent
import mtg.game.state.{GameState, InternalGameAction, InternalGameActionResult}
import mtg.game.turns.TurnPhase

case class BeginPhaseAction(phase: TurnPhase) extends InternalGameAction {
  override def execute(currentGameState: GameState): InternalGameActionResult = {
      (phase.actions :+ EndPhaseAction(phase), BeginPhaseEvent(phase))
  }
}

case class BeginPhaseEvent(phase: TurnPhase) extends GameEvent
