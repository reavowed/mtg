package mtg.parts.damage

import mtg.characteristics.types.Type
import mtg.events.LoseLifeEvent
import mtg.game.state.{InternalGameAction, GameActionResult, GameState}
import mtg.game.{ObjectId, ObjectOrPlayer, PlayerId}

import scala.collection.mutable.ListBuffer

case class DealDamageEvent(source: ObjectId, recipient: ObjectOrPlayer, amount: Int) extends InternalGameAction {
  override def execute(gameState: GameState): GameActionResult = {
    recipient match {
      case objectId: ObjectId =>
        val characteristics = objectId.currentCharacteristics(gameState)
        val results = ListBuffer[InternalGameAction]()
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
