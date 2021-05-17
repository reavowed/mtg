package mtg.game.turns

import mtg.game.state.history.{GameHistory, LogEvent}
import mtg.game.state.{GameAction, GameState, TurnCycleEvent}

case class BeginStepEvent(step: TurnStep) extends TurnCycleEvent {
  override def execute(currentGameState: GameState): (GameHistory, Seq[GameAction], Option[LogEvent]) = {
    (
      currentGameState.gameHistory.startStep(step),
      step.actions,
      None
    )
  }
}
