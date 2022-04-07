package mtg.actions

import mtg.core.ObjectId
import mtg.game.objects.GameObjectState
import mtg.game.state.{DirectGameObjectAction, GameState}

case class TapObjectAction(objectId: ObjectId) extends DirectGameObjectAction {
  override def execute(implicit gameState: GameState): GameObjectState = {
    gameState.gameObjectState.updatePermanentObject(
      objectId,
      _.updatePermanentStatus(_.tap()))
  }
  override def canBeReverted: Boolean = true
}
