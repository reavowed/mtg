package mtg.stack.adding

import mtg.game.PlayerId
import mtg.game.objects.ManaObject
import mtg.game.state.{GameActionResult, GameState, InternalGameAction}

case class SpendManaAutomaticallyEvent(player: PlayerId, remainingMana: Seq[ManaObject]) extends InternalGameAction {
  override def execute(gameState: GameState): GameActionResult = {
    gameState.gameObjectState.updateManaPool(player, _ => remainingMana)
  }

  override def canBeReverted: Boolean = true
}
