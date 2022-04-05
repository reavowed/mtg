package mtg.actions.damage

import mtg.core.ObjectId
import mtg.game.state.{GameActionResult, GameState, GameObjectAction}

case class MarkDamageAction(sourceId: ObjectId, creatureId: ObjectId, amount: Int) extends GameObjectAction {
  override def execute(gameState: GameState): GameActionResult = {
    gameState.gameObjectState.updatePermanentObject(creatureId, _.updateMarkedDamage(_ + amount))
  }
  override def canBeReverted: Boolean = true
}
