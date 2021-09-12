package mtg.game.turns.turnBasedActions

import mtg.game.state._
import mtg.game.turns.Turn

object CleanupAction extends InternalGameAction {
  override def execute(currentGameState: GameState): InternalGameActionResult = {
    Seq(DamageWearsOffEvent, UntilEndOfTurnEffectsEnd(currentGameState.currentTurn.get))
  }
  override def canBeReverted: Boolean = true
}

object DamageWearsOffEvent extends GameObjectEvent {
  override def execute(currentGameState: GameState): GameObjectEventResult = {
    currentGameState.gameObjectState.battlefield.foldLeft(currentGameState.gameObjectState) { (state, obj) =>
      state.updatePermanentObject(obj.objectId, _.updateMarkedDamage(_ => 0))
    }
  }
  override def canBeReverted: Boolean = true
}

case class UntilEndOfTurnEffectsEnd(turn: Turn) extends InternalGameAction {
  override def execute(currentGameState: GameState): InternalGameActionResult = ()
  override def canBeReverted: Boolean = true
}
