package mtg.game.turns

import mtg.game.state.history.{GameHistory, LogEvent}
import mtg.game.state.{GameAction, GameState, TurnCycleEvent}

case class BeginPhaseEvent(phase: TurnPhase) extends TurnCycleEvent {
  override def execute(currentGameState: GameState): (GameHistory, Seq[GameAction], Option[LogEvent]) = {
    (
      currentGameState.gameHistory.startPhase(phase),
      phase.actions,
      None
    )
  }
}
