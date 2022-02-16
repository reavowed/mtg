package mtg.parts.damage

import mtg.characteristics.types.Type
import mtg.events.LoseLifeEvent
import mtg.game.state.{CurrentCharacteristics, GameActionResult, GameState, InternalGameAction}
import mtg.game.{ObjectId, ObjectOrPlayer, PlayerId}

import scala.collection.mutable.ListBuffer

case class DealDamageEvent(source: ObjectId, recipient: ObjectOrPlayer, amount: Int) extends InternalGameAction {
  override def execute(gameState: GameState): GameActionResult = {
    recipient match {
      case objectId: ObjectId =>
        val types = CurrentCharacteristics.getCharacteristics(objectId, gameState).types
        val results = ListBuffer[InternalGameAction]()
        if (types.contains(Type.Creature)) {
          results.addOne(MarkDamageEvent(source, objectId, amount))
        }
        results.result()
      case playerIdentifier: PlayerId =>
        LoseLifeEvent(playerIdentifier, amount)
    }
  }
  override def canBeReverted: Boolean = true
}
