package mtg.game.turns.priority

import mtg.game.state.history.LogEvent
import mtg.game.state.{GameAction, GameState, InternalGameAction, GameActionResult}

case object PriorityFromActivePlayerAction extends InternalGameAction {
  override def execute(currentGameState: GameState): GameActionResult = {
    PriorityFromPlayerAction(currentGameState.activePlayer).execute(currentGameState)
  }
}
