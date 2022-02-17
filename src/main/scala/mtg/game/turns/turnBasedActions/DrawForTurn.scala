package mtg.game.turns.turnBasedActions

import mtg.actions.DrawCardAction
import mtg.game.state.history.LogEvent
import mtg.game.state.{ExecutableGameAction, GameState, PartialGameActionResult, WrappedOldUpdates}

case object DrawForTurn extends ExecutableGameAction[Any] {
  override def execute()(implicit gameState: GameState): PartialGameActionResult[Any] = {
    // TODO: Only log event if card was drawn
    PartialGameActionResult.children(
      WrappedOldUpdates(DrawCardAction(gameState.activePlayer)),
      LogEvent.DrawForTurn(gameState.activePlayer)
    )
  }
}
