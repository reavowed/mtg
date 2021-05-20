package mtg.game.turns

import mtg.game.state._
import mtg.game.state.history.{GameHistory, LogEvent}

case class BeginTurnEvent(turn: Turn) extends TurnCycleEvent {
  override def execute(currentGameState: GameState): (GameHistory => GameHistory, Seq[GameAction], Option[LogEvent]) = {
    val newTurnNumber = currentGameState.currentTurnNumber + 1
    (
      _.startTurn(turn),
      TurnPhase.All.map(BeginPhaseEvent),
      Some(LogEvent.NewTurn(turn.activePlayer, newTurnNumber))
    )
  }
}
