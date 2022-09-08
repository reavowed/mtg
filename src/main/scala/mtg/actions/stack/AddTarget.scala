package mtg.actions.stack

import mtg.definitions.{ObjectId, ObjectOrPlayerId}
import mtg.game.state.{DirectGameObjectAction, GameState}

case class AddTarget(stackObjectId: ObjectId, target: ObjectOrPlayerId) extends DirectGameObjectAction[Unit] {
  override def execute(implicit gameState: GameState): DirectGameObjectAction.Result[Unit] = {
    gameState.gameObjectState.updateStackObject(stackObjectId, _.addTarget(target))
  }
  override def canBeReverted: Boolean = true
}
