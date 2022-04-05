package mtg.actions

import mtg.core.PlayerId
import mtg.game.objects.ManaObject
import mtg.game.state.{GameActionResult, GameState, GameObjectAction}

case class RemoveManaAction(player: PlayerId, manaToRemove: Seq[ManaObject]) extends GameObjectAction {
  override def execute(gameState: GameState): GameActionResult = {
    gameState.gameObjectState.updateManaPool(player, pool => pool.diff(manaToRemove))
  }

  override def canBeReverted: Boolean = true
}
