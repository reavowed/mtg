package mtg.game.turns.turnBasedActions

import mtg.game.state.history.LogEvent
import mtg.game.state.{GameAction, GameObjectEvent, GameObjectEventResult, GameState, InternalGameAction}

object CleanupAction extends InternalGameAction {
  override def execute(currentGameState: GameState): (Seq[GameAction], Option[LogEvent]) = (Seq(DamageWearsOffEvent), None)
}

object DamageWearsOffEvent extends GameObjectEvent {
  override def execute(currentGameState: GameState): GameObjectEventResult = {
    currentGameState.gameObjectState.battlefield.foldLeft(currentGameState.gameObjectState) { (state, obj) =>
      state.updateGameObject(obj.objectId, _.updateMarkedDamage(_ => 0))
    }
  }
}
