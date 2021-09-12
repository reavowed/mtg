package mtg.game.turns.turnEvents

import mtg.game.state.history.LogEvent
import mtg.game.state.{GameState, InternalGameAction, InternalGameActionResult}
import mtg.game.turns.{Turn, TurnPhase}

case class BeginTurnEvent(turn: Turn) extends InternalGameAction {
  override def execute(currentGameState: GameState): InternalGameActionResult = {
    (TurnPhase.All.map(BeginPhaseEvent), LogEvent.NewTurn(turn))
  }
}
