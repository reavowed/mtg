package mtg.game.turns.turnEvents

import mtg.game.state.history.{GameHistory, LogEvent}
import mtg.game.state.{GameAction, GameState, GameActionResult, TurnCycleEvent}
import mtg.game.turns.{Turn, TurnPhase}

case class BeginTurnEvent(turn: Turn) extends TurnCycleEvent {
  override def execute(currentGameState: GameState): (GameHistory => GameHistory, GameActionResult) = {
    val newTurnNumber = currentGameState.currentTurnNumber + 1
    (
      _.startTurn(turn),
      (TurnPhase.All.map(BeginPhaseEvent), LogEvent.NewTurn(turn.activePlayer, newTurnNumber))
    )
  }
}
