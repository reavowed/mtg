package mtg.game.turns.turnEvents

import mtg.game.state.{GameActionResult, GameState, TurnCycleEvent, TurnState}
import mtg.game.turns.TurnPhase

case class BeginPhaseEvent(phase: TurnPhase) extends TurnCycleEvent {
  override def execute(currentGameState: GameState): (TurnState, GameActionResult) = {
    (
      currentGameState.turnState.startPhase(phase),
      phase.actions :+ EndPhaseEvent(phase)
    )
  }
}
