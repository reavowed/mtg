package mtg.events

import mtg.game.state.{GameObjectAction, GameObjectActionResult, GameState}

object EmptyManaPoolsAction extends GameObjectAction {
  override def execute(currentGameState: GameState): GameObjectActionResult = {
    currentGameState.playersInApnapOrder.foldLeft(currentGameState.gameObjectState) { (gameObjectState, player) =>
      gameObjectState.updateManaPool(player, _ => Nil)
    }
  }
}
