package mtg.actions

import mtg.core.PlayerId
import mtg.game.objects.{GameObjectState, ManaObject}
import mtg.game.state.{DirectGameObjectAction, GameState}

case class RemoveManaAction(player: PlayerId, manaToRemove: Seq[ManaObject]) extends DirectGameObjectAction[Unit] {
  override def execute(implicit gameState: GameState): DirectGameObjectAction.Result[Unit] = {
    gameState.gameObjectState.updateManaPool(player, pool => pool.diff(manaToRemove))
  }
  override def canBeReverted: Boolean = true
}
