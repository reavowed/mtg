package mtg.parts.damage

import mtg.game.ObjectId
import mtg.game.state.{GameObjectEvent, GameObjectEventResult, GameState}

case class MarkDamageEvent(sourceId: ObjectId, creatureId: ObjectId, amount: Int) extends GameObjectEvent {
  override def execute(currentGameState: GameState): GameObjectEventResult = {
    currentGameState.gameObjectState.updatePermanentObject(creatureId, _.updateMarkedDamage(_ + amount))
  }
  override def canBeReverted: Boolean = true
}
