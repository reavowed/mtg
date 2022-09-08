package mtg.actions.damage

import mtg.actions.LoseLifeAction
import mtg.definitions.types.Type
import mtg.definitions.{ObjectId, ObjectOrPlayerId, PlayerId}
import mtg.game.state.{CurrentCharacteristics, DelegatingGameObjectAction, GameObjectAction, GameState}

import scala.collection.mutable.ListBuffer

case class DealDamageAction(source: ObjectId, recipient: ObjectOrPlayerId, amount: Int) extends DelegatingGameObjectAction {
  override def delegate(implicit gameState: GameState): Seq[GameObjectAction[_]] = {
    recipient match {
      case objectId: ObjectId =>
        val types = CurrentCharacteristics.getCharacteristics(objectId, gameState).types
        val results = ListBuffer[GameObjectAction[_]]()
        if (types.contains(Type.Creature)) {
          results.addOne(MarkDamageAction(source, objectId, amount))
        }
        results.result()
      case playerIdentifier: PlayerId =>
        LoseLifeAction(playerIdentifier, amount)
    }
  }
}
