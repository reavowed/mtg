package mtg.actions

import mtg.game.objects.GameObjectState
import mtg.game.state.{DirectGameObjectAction, GameState}

object EmptyManaPoolsAction extends DirectGameObjectAction[Unit] {
  override def execute(implicit gameState: GameState): DirectGameObjectAction.Result[Unit] = {
    gameState.playersInApnapOrder.foldLeft(gameState.gameObjectState) { (gameObjectState, player) =>
      gameObjectState.updateManaPool(player, _ => Nil)
    }
  }
  override def canBeReverted: Boolean = true
}
