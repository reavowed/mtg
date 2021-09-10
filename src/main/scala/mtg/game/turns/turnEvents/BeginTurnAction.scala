package mtg.game.turns.turnEvents

import mtg.game.state.history.{GameHistory, LogEvent}
import mtg.game.state.{GameAction, GameActionResult, GameState, TurnCycleAction, TurnState}
import mtg.game.turns.{Turn, TurnPhase}

case class BeginTurnAction(turn: Turn) extends TurnCycleAction {
  override def execute(currentGameState: GameState): (TurnState, GameActionResult) = {
    val newTurnState = currentGameState.turnState.startTurn(turn)
    (
      newTurnState,
      (TurnPhase.All.map(BeginPhaseAction), LogEvent.NewTurn(turn.activePlayer, newTurnState.currentTurnNumber))
    )
  }
}
