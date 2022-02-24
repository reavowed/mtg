package mtg.stack.adding

import mtg.core.PlayerId
import mtg.game.objects.ManaObject
import mtg.game.state.{GameActionResult, GameState, InternalGameAction}

case class RemoveManaAction(player: PlayerId, manaToRemove: Seq[ManaObject]) extends InternalGameAction {
  override def execute(gameState: GameState): GameActionResult = {
    gameState.gameObjectState.updateManaPool(player, pool => pool.diff(manaToRemove))
  }

  override def canBeReverted: Boolean = true
}
