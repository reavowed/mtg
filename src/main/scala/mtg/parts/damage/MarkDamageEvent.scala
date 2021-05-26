package mtg.parts.damage

import mtg.game.objects.ObjectId
import mtg.game.state.{GameObjectEvent, GameObjectEventResult, GameState}

case class MarkDamageEvent(sourceId: ObjectId, creatureId: ObjectId, amount: Int) extends GameObjectEvent {
  override def execute(currentGameState: GameState): GameObjectEventResult = {
    currentGameState.gameObjectState.updateGameObject(creatureId, _.updateMarkedDamage(_ + amount))
  }
}
