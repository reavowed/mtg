package mtg.parts.damage

import mtg.characteristics.types.Type
import mtg.events.LoseLifeAction
import mtg.game.state.{GameObjectAction, GameObjectActionResult, GameState}
import mtg.game.{ObjectId, ObjectOrPlayer, PlayerId}

import scala.collection.mutable.ListBuffer

case class DealDamageAction(source: ObjectId, recipient: ObjectOrPlayer, amount: Int) extends GameObjectAction {
  override def execute(currentGameState: GameState): GameObjectActionResult = {
    recipient match {
      case objectId: ObjectId =>
        val characteristics = objectId.currentCharacteristics(currentGameState)
        val results = ListBuffer[GameObjectAction]()
        if (characteristics.types.contains(Type.Creature)) {
          results.addOne(MarkDamageAction(source, objectId, amount))
        }
        results.result()
      case playerIdentifier: PlayerId =>
        LoseLifeAction(playerIdentifier, amount)
    }
  }
}
