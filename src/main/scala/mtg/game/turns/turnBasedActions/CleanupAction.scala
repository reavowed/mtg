package mtg.game.turns.turnBasedActions

import mtg.game.state._
import mtg.game.turns.Turn

object CleanupAction extends ExecutableGameAction[Unit] {
  override def execute()(implicit gameState: GameState): PartialGameActionResult[Unit] = {
    // TODO: These two actions should be simultaneous
    PartialGameActionResult.child(WrappedOldUpdates(DamageWearsOffEvent, UntilEndOfTurnEffectsEnd))
  }
}

object DamageWearsOffEvent extends InternalGameAction {
  override def execute(gameState: GameState): GameActionResult = {
    gameState.gameObjectState.battlefield.foldLeft(gameState.gameObjectState) { (state, obj) =>
      state.updatePermanentObject(obj.objectId, _.updateMarkedDamage(_ => 0))
    }
  }
  override def canBeReverted: Boolean = true
}

object UntilEndOfTurnEffectsEnd extends InternalGameAction {
  override def execute(gameState: GameState): GameActionResult = ()
  override def canBeReverted: Boolean = true
}
