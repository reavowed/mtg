package mtg.actions.damage

import mtg.actions.LoseLifeAction
import mtg.core.types.Type
import mtg.core.{ObjectId, ObjectOrPlayerId, PlayerId}
import mtg.game.state.{CurrentCharacteristics, DelegatingGameObjectAction, GameObjectAction, GameState}

import scala.collection.mutable.ListBuffer

case class DealDamageAction(source: ObjectId, recipient: ObjectOrPlayerId, amount: Int) extends DelegatingGameObjectAction {
  override def delegate(implicit gameState: GameState): Seq[GameObjectAction] = {
    recipient match {
      case objectId: ObjectId =>
        val types = CurrentCharacteristics.getCharacteristics(objectId, gameState).types
        val results = ListBuffer[GameObjectAction]()
        if (types.contains(Type.Creature)) {
          results.addOne(MarkDamageAction(source, objectId, amount))
        }
        results.result()
      case playerIdentifier: PlayerId =>
        LoseLifeAction(playerIdentifier, amount)
    }
  }
}
