package mtg.game.turns.turnBasedActions

import mtg.game.state._
import mtg.game.turns.Turn

object CleanupAction extends InternalGameAction {
  override def execute(currentGameState: GameState): GameActionResult = {
    Seq(DamageWearsOffAction, UntilEndOfTurnEffectsEnd(currentGameState.turnState.currentTurn.get))
  }
}

object DamageWearsOffAction extends GameObjectAction {
  override def execute(currentGameState: GameState): GameObjectActionResult = {
    currentGameState.gameObjectState.battlefield.foldLeft(currentGameState.gameObjectState) { (state, obj) =>
      state.updatePermanentObject(obj.objectId, _.updateMarkedDamage(_ => 0))
    }
  }
}

case class UntilEndOfTurnEffectsEnd(turn: Turn) extends GameObjectAction {
  override def execute(currentGameState: GameState): GameObjectActionResult = ()
}
