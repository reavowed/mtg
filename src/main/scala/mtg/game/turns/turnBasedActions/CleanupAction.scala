package mtg.game.turns.turnBasedActions

import mtg.game.state.{GameObjectAction, GameObjectActionResult, GameState, InternalGameAction, InternalGameActionResult}
import mtg.game.state.history.GameEvent

object CleanupAction extends InternalGameAction {
  override def execute(currentGameState: GameState): InternalGameActionResult = {
    Seq(DamageWearsOffAction, EndTurnEffectsAction)
  }
}

object DamageWearsOffAction extends GameObjectAction {
  override def execute(currentGameState: GameState): GameObjectActionResult = {
    currentGameState.gameObjectState.battlefield.foldLeft(currentGameState.gameObjectState) { (state, obj) =>
      state.updatePermanentObject(obj.objectId, _.updateMarkedDamage(_ => 0))
    }
  }
}

object EndTurnEffectsAction extends InternalGameAction {
  override def execute(currentGameState: GameState): InternalGameActionResult = EndTurnEffectsEvent
}

object EndTurnEffectsEvent extends GameEvent




