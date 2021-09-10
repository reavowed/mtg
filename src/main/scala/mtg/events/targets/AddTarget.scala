package mtg.events.targets

import mtg.game.state.{GameObjectAction, GameObjectActionResult, GameState}
import mtg.game.{ObjectId, ObjectOrPlayer}

case class AddTarget(stackObjectId: ObjectId, target: ObjectOrPlayer) extends GameObjectAction {
  override def execute(currentGameState: GameState): GameObjectActionResult = {
    currentGameState.gameObjectState.updateStackObject(stackObjectId, _.addTarget(target))
  }
}
