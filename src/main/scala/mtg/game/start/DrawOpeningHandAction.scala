package mtg.game.start

import mtg.core.PlayerId
import mtg.events.DrawCardsEvent
import mtg.game.state.{ExecutableGameAction, GameState, PartialGameActionResult, WrappedOldUpdates}

case class DrawOpeningHandAction(player: PlayerId) extends ExecutableGameAction[Unit] {
  override def execute()(implicit gameState: GameState): PartialGameActionResult[Unit] = {
    PartialGameActionResult.child(WrappedOldUpdates(DrawCardsEvent(player, gameState.gameData.startingHandSize)))
  }
}
