package mtg.game.turns.turnBasedActions

import mtg.events.DrawCardEvent
import mtg.game.state.history.LogEvent
import mtg.game.state.{ExecutableGameAction, GameActionResult, GameState, InternalGameAction, PartialGameActionResult, WrappedOldUpdates}

case object DrawForTurn extends ExecutableGameAction[Any] {
  override def execute()(implicit gameState: GameState): PartialGameActionResult[Any] = {
    // TODO: Only log event if card was drawn
    PartialGameActionResult.children(
      WrappedOldUpdates(DrawCardEvent(gameState.activePlayer)),
      LogEvent.DrawForTurn(gameState.activePlayer)
    )
  }
}
