package mtg.game.turns.turnEvents

import mtg.game.state.history.{GameHistory, LogEvent}
import mtg.game.state.{GameAction, GameState, GameActionResult, TurnCycleEvent}
import mtg.game.turns.TurnPhase

case class BeginPhaseEvent(phase: TurnPhase) extends TurnCycleEvent {
  override def execute(currentGameState: GameState): (GameHistory => GameHistory, GameActionResult) = {
    (
      _.startPhase(phase),
      phase.actions :+ EndPhaseEvent(phase)
    )
  }
}
