package mtg.game.start

import mtg.events.DrawCardsEvent
import mtg.game.state.{GameState, NewGameActionResult, RootGameAction, WrappedOldUpdates}

object DrawOpeningHandsAction extends RootGameAction {
  override def execute()(implicit gameState: GameState): NewGameActionResult.Partial[RootGameAction] = {
    val players = gameState.gameData.playersInTurnOrder
    NewGameActionResult.Delegated.valueAfterChildren(
      MulligansAction(players, 0),
      players.map(DrawOpeningHandAction))
  }
}
