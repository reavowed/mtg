package mtg.game.start

import mtg.game.state.{GameState, PartialGameActionResult, RootGameAction}

object DrawOpeningHandsAction extends RootGameAction {
  override def execute()(implicit gameState: GameState): PartialGameActionResult[RootGameAction] = {
    val players = gameState.gameData.playersInTurnOrder
    PartialGameActionResult.childrenThenValue(
      players.map(DrawOpeningHandAction),
      MulligansAction(players, 0))
  }
}
