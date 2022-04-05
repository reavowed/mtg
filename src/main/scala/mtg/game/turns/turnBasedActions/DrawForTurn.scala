package mtg.game.turns.turnBasedActions

import mtg.actions.DrawCardAction
import mtg.game.state.history.LogEvent
import mtg.game.state.{DelegatingGameAction, GameAction, GameState}

case object DrawForTurn extends DelegatingGameAction[Unit] {
  override def delegate(implicit gameState: GameState): GameAction[Unit] = {
    // TODO: Only log event if card was drawn
    for {
      _ <- DrawCardAction(gameState.activePlayer)
      _ <- LogEvent.DrawForTurn(gameState.activePlayer)
    } yield ()
  }
}
