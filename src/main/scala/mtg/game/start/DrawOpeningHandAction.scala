package mtg.game.start

import mtg.core.PlayerId
import mtg.actions.DrawCardsAction
import mtg.game.state.{ExecutableGameAction, GameState, PartialGameActionResult, WrappedOldUpdates}

case class DrawOpeningHandAction(player: PlayerId) extends ExecutableGameAction[Unit] {
  override def execute()(implicit gameState: GameState): PartialGameActionResult[Unit] = {
    PartialGameActionResult.child(WrappedOldUpdates(DrawCardsAction(player, gameState.gameData.startingHandSize)))
  }
}
