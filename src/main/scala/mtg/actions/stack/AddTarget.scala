package mtg.actions.stack

import mtg.core.{ObjectId, ObjectOrPlayerId}
import mtg.game.state.{GameActionResult, GameState, GameObjectAction}

case class AddTarget(stackObjectId: ObjectId, target: ObjectOrPlayerId) extends GameObjectAction {
  override def execute(gameState: GameState): GameActionResult = {
    gameState.gameObjectState.updateStackObject(stackObjectId, _.addTarget(target))
  }

  override def canBeReverted: Boolean = true
}
