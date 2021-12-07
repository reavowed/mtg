package mtg.game.start

import mtg.game.state.{GameState, NewGameActionResult, RootGameAction}

object DrawOpeningHandsAction extends RootGameAction {
  override def execute()(implicit gameState: GameState): NewGameActionResult.Partial[RootGameAction] = {
    val players = gameState.gameData.playersInTurnOrder
    NewGameActionResult.Delegated.childrenThenValue(
      players.map(DrawOpeningHandAction),
      MulligansAction(players, 0))
  }
}
