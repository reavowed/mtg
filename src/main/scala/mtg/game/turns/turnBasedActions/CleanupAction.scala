package mtg.game.turns.turnBasedActions

import mtg.game.state._
import mtg.game.turns.Turn

object CleanupAction extends InternalGameAction {
  override def execute(currentGameState: GameState): GameActionResult = {
    Seq(DamageWearsOffEvent, UntilEndOfTurnEffectsEnd(currentGameState.currentTurn.get))
  }
}

object DamageWearsOffEvent extends GameObjectEvent {
  override def execute(currentGameState: GameState): GameObjectEventResult = {
    currentGameState.gameObjectState.battlefield.foldLeft(currentGameState.gameObjectState) { (state, obj) =>
      state.updatePermanentObject(obj.objectId, _.updateMarkedDamage(_ => 0))
    }
  }
}

case class UntilEndOfTurnEffectsEnd(turn: Turn) extends GameObjectEvent {
  override def execute(currentGameState: GameState): GameObjectEventResult = ()
}
