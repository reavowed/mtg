package mtg.game.turns.turnBasedActions

import mtg.events.DrawCardAction
import mtg.game.state.history.LogEvent
import mtg.game.state.{GameAction, GameState, InternalGameAction, GameActionResult}

case object DrawForTurn extends InternalGameAction {
  override def execute(currentGameState: GameState): GameActionResult = {
    (DrawCardAction(currentGameState.activePlayer), LogEvent.DrawForTurn(currentGameState.activePlayer))
  }
}
