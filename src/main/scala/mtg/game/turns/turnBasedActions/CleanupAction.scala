package mtg.game.turns.turnBasedActions

import mtg.game.state.history.LogEvent
import mtg.game.state.{GameAction, GameObjectEvent, GameObjectEventResult, GameState, InternalGameAction, InternalGameActionResult}

object CleanupAction extends InternalGameAction {
  override def execute(currentGameState: GameState): InternalGameActionResult = DamageWearsOffEvent
}

object DamageWearsOffEvent extends GameObjectEvent {
  override def execute(currentGameState: GameState): GameObjectEventResult = {
    currentGameState.gameObjectState.battlefield.foldLeft(currentGameState.gameObjectState) { (state, obj) =>
      state.updateGameObject(obj.objectId, _.updateMarkedDamage(_ => 0))
    }
  }
}
