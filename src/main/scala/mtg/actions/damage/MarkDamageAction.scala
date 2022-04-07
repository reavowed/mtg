package mtg.actions.damage

import mtg.core.ObjectId
import mtg.game.objects.GameObjectState
import mtg.game.state.{DirectGameObjectAction, GameState}

case class MarkDamageAction(sourceId: ObjectId, creatureId: ObjectId, amount: Int) extends DirectGameObjectAction {
  override def execute(implicit gameState: GameState): GameObjectState = {
    gameState.gameObjectState.updatePermanentObject(creatureId, _.updateMarkedDamage(_ + amount))
  }
  override def canBeReverted: Boolean = true
}
