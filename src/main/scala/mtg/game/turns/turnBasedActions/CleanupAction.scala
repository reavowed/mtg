package mtg.game.turns.turnBasedActions

import mtg.game.state._

object CleanupAction extends InternalGameAction {
  override def execute(currentGameState: GameState): InternalGameActionResult = {
    Seq(DamageWearsOffAction, UntilEndOfTurnEffectsEnd)
  }
}

object DamageWearsOffAction extends GameObjectAction {
  override def execute(currentGameState: GameState): GameObjectActionResult = {
    currentGameState.gameObjectState.battlefield.foldLeft(currentGameState.gameObjectState) { (state, obj) =>
      state.updatePermanentObject(obj.objectId, _.updateMarkedDamage(_ => 0))
    }
  }
}

object UntilEndOfTurnEffectsEnd extends GameObjectAction {
  override def execute(currentGameState: GameState): GameObjectActionResult = ()
}
