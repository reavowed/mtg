package mtg.parts.damage

import mtg.core.ObjectId
import mtg.game.state.{GameActionResult, GameState, InternalGameAction}

case class MarkDamageEvent(sourceId: ObjectId, creatureId: ObjectId, amount: Int) extends InternalGameAction {
  override def execute(gameState: GameState): GameActionResult = {
    gameState.gameObjectState.updatePermanentObject(creatureId, _.updateMarkedDamage(_ + amount))
  }
  override def canBeReverted: Boolean = true
}
