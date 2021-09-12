package mtg.events.targets

import mtg.game.state.{GameObjectEvent, GameObjectEventResult, GameState}
import mtg.game.{ObjectId, ObjectOrPlayer}

case class AddTarget(stackObjectId: ObjectId, target: ObjectOrPlayer) extends GameObjectEvent {
  override def execute(currentGameState: GameState): GameObjectEventResult = {
    currentGameState.gameObjectState.updateStackObject(stackObjectId, _.addTarget(target))
  }
  override def canBeReverted: Boolean = true
}
