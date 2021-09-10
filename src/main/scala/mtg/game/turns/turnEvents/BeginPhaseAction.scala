package mtg.game.turns.turnEvents

import mtg.game.state.{GameActionResult, GameState, TurnCycleAction, TurnState}
import mtg.game.turns.TurnPhase

case class BeginPhaseAction(phase: TurnPhase) extends TurnCycleAction {
  override def execute(currentGameState: GameState): (TurnState, GameActionResult) = {
    (
      currentGameState.turnState.startPhase(phase),
      phase.actions :+ EndPhaseAction(phase)
    )
  }
}
