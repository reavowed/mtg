package mtg.game.turns.turnEvents

import mtg.game.state.{InternalGameActionResult, GameState, TurnCycleAction, TurnState}
import mtg.game.turns.TurnPhase

case class BeginPhaseAction(phase: TurnPhase) extends TurnCycleAction {
  override def execute(currentGameState: GameState): (TurnState, InternalGameActionResult) = {
    (
      currentGameState.turnState.startPhase(phase),
      phase.actions :+ EndPhaseAction(phase)
    )
  }
}
