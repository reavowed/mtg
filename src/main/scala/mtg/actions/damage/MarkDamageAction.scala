package mtg.actions.damage

import mtg.definitions.ObjectId
import mtg.game.state.{DirectGameObjectAction, GameState}

case class MarkDamageAction(sourceId: ObjectId, creatureId: ObjectId, amount: Int) extends DirectGameObjectAction[Unit] {
  override def execute(implicit gameState: GameState): DirectGameObjectAction.Result[Unit] = {
    gameState.gameObjectState.updatePermanentObject(creatureId, _.updateMarkedDamage(_ + amount))
  }
  override def canBeReverted: Boolean = true
}
