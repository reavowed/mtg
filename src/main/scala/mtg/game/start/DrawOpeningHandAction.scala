package mtg.game.start

import mtg.events.DrawCardsEvent
import mtg.game.PlayerId
import mtg.game.state.{ExecutableGameAction, GameState, NewGameActionResult, RootGameAction, WrappedOldUpdates}

case class DrawOpeningHandAction(player: PlayerId) extends ExecutableGameAction[Unit] {
  override def execute()(implicit gameState: GameState): NewGameActionResult.Partial[Unit] = {
    NewGameActionResult.Delegated.directly(WrappedOldUpdates(DrawCardsEvent(player, gameState.gameData.startingHandSize)))
  }
}
