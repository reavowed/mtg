package mtg.actions

import mtg.game.state.{GameActionResult, GameState, InternalGameAction}

object EmptyManaPoolsAction extends InternalGameAction {
  override def execute(gameState: GameState): GameActionResult = {
    gameState.playersInApnapOrder.foldLeft(gameState.gameObjectState) { (gameObjectState, player) =>
      gameObjectState.updateManaPool(player, _ => Nil)
    }
  }
  override def canBeReverted: Boolean = true
}
