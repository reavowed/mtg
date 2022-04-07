package mtg.game.turns.turnBasedActions

import mtg.game.objects.GameObjectState
import mtg.game.state._

object CleanupAction extends DelegatingGameAction[Unit] {
  override def delegate(implicit gameState: GameState): GameAction[Unit] = {
    // TODO: These two actions should be simultaneous
    Seq(DamageWearsOffEvent, EndOfTurnEffectsEnd).traverse
  }
}

object DamageWearsOffEvent extends DirectGameObjectAction {
  override def execute(implicit gameState: GameState): GameObjectState = {
    gameState.gameObjectState.battlefield.foldLeft(gameState.gameObjectState) { (state, obj) =>
      state.updatePermanentObject(obj.objectId, _.updateMarkedDamage(_ => 0))
    }
  }
  override def canBeReverted: Boolean = true
}

object EndOfTurnEffectsEnd extends DirectGameObjectAction {
  override def execute(implicit gameState: GameState): GameObjectState = None
  override def canBeReverted: Boolean = true
}
