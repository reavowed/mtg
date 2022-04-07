package mtg.actions.stack

import mtg.core.{ObjectId, ObjectOrPlayerId}
import mtg.game.objects.GameObjectState
import mtg.game.state.{DirectGameObjectAction, GameState}

case class AddTarget(stackObjectId: ObjectId, target: ObjectOrPlayerId) extends DirectGameObjectAction {
  override def execute(implicit gameState: GameState): GameObjectState = {
    gameState.gameObjectState.updateStackObject(stackObjectId, _.addTarget(target))
  }
  override def canBeReverted: Boolean = true
}
