package mtg.parts.damage

import mtg.characteristics.types.Type
import mtg.events.LoseLifeEvent
import mtg.game.state.{GameObjectEvent, GameObjectEventResult, GameState}
import mtg.game.{ObjectId, ObjectOrPlayer, PlayerId}

import scala.collection.mutable.ListBuffer

case class DealDamageEvent(source: ObjectId, recipient: ObjectOrPlayer, amount: Int) extends GameObjectEvent {
  override def execute(currentGameState: GameState): GameObjectEventResult = {
    recipient match {
      case objectId: ObjectId =>
        val characteristics = objectId.currentCharacteristics(currentGameState)
        val results = ListBuffer[GameObjectEvent]()
        if (characteristics.types.contains(Type.Creature)) {
          results.addOne(MarkDamageEvent(source, objectId, amount))
        }
        results.result()
      case playerIdentifier: PlayerId =>
        LoseLifeEvent(playerIdentifier, amount)
    }
  }
  override def canBeReverted: Boolean = true
}
