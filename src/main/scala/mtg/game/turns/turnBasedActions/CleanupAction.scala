package mtg.game.turns.turnBasedActions

import mtg.game.state._
import mtg.game.turns.Turn

object CleanupAction extends InternalGameAction {
  override def execute(gameState: GameState): GameActionResult = {
    Seq(DamageWearsOffEvent, UntilEndOfTurnEffectsEnd(gameState.currentTurn.get))
  }
  override def canBeReverted: Boolean = true
}

object DamageWearsOffEvent extends InternalGameAction {
  override def execute(gameState: GameState): GameActionResult = {
    gameState.gameObjectState.battlefield.foldLeft(gameState.gameObjectState) { (state, obj) =>
      state.updatePermanentObject(obj.objectId, _.updateMarkedDamage(_ => 0))
    }
  }
  override def canBeReverted: Boolean = true
}

case class UntilEndOfTurnEffectsEnd(turn: Turn) extends InternalGameAction {
  override def execute(gameState: GameState): GameActionResult = ()
  override def canBeReverted: Boolean = true
}
