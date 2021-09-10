package mtg.game.turns.turnEvents

import mtg.game.state.history.{GameHistory, LogEvent}
import mtg.game.state.{GameAction, GameActionResult, GameState, TurnCycleEvent, TurnState}
import mtg.game.turns.{Turn, TurnPhase}

case class BeginTurnEvent(turn: Turn) extends TurnCycleEvent {
  override def execute(currentGameState: GameState): (TurnState, GameActionResult) = {
    val newTurnState = currentGameState.turnState.startTurn(turn)
    (
      newTurnState,
      (TurnPhase.All.map(BeginPhaseEvent), LogEvent.NewTurn(turn.activePlayer, newTurnState.currentTurnNumber))
    )
  }
}
