package mtg.game.start

import mtg.actions.DrawCardsAction
import mtg.core.PlayerId
import mtg.game.state.{DelegatingGameAction, GameAction, GameState}

case class DrawOpeningHandAction(player: PlayerId) extends DelegatingGameAction[Unit] {
  override def delegate(implicit gameState: GameState): GameAction[Unit] = {
    DrawCardsAction(player, gameState.gameData.startingHandSize)
  }
}
