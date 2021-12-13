package mtg.game.turns.turnEvents

import mtg.game.state.history.LogEvent
import mtg.game.state.{GameActionResult, GameState, InternalGameAction}
import mtg.game.turns.{Turn, TurnPhase}

case class BeginTurnEvent(turn: Turn) extends InternalGameAction {
  override def execute(gameState: GameState): GameActionResult = {
    (TurnPhase.All.map(BeginPhaseEvent), LogEvent.NewTurn(turn))
  }
  override def canBeReverted: Boolean = false
}
