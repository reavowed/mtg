package mtg.game.turns.turnEvents

import mtg.game.state.history.{GameHistory, LogEvent}
import mtg.game.state.{GameAction, GameState, TurnCycleEvent}
import mtg.game.turns.{Turn, TurnPhase}

case class EndOfTurnEvent(turn: Turn) extends TurnCycleEvent {
  override def execute(currentGameState: GameState): (GameHistory => GameHistory, Seq[GameAction], Option[LogEvent]) = {
    val newTurnNumber = currentGameState.currentTurnNumber + 1
    (
      _.startTurn(turn),
      TurnPhase.All.map(BeginPhaseEvent),
      Some(LogEvent.NewTurn(turn.activePlayer, newTurnNumber))
    )
  }
}
