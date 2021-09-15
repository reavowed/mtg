package mtg.game.turns.turnBasedActions

import mtg.events.DrawCardEvent
import mtg.game.state.history.LogEvent
import mtg.game.state.{GameState, InternalGameAction, GameActionResult}

case object DrawForTurn extends InternalGameAction {
  override def execute(gameState: GameState): GameActionResult = {
    (DrawCardEvent(gameState.activePlayer), LogEvent.DrawForTurn(gameState.activePlayer))
  }
  override def canBeReverted: Boolean = false
}
