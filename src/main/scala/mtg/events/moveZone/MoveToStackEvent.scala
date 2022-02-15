package mtg.events.moveZone

import mtg.game.objects.{GameObjectState, StackObject}
import mtg.game.state.ObjectWithState
import mtg.game.{ObjectId, PlayerId}

case class MoveToStackEvent(objectId: ObjectId, player: PlayerId) extends MoveObjectEvent[StackObject] {
  override def createNewObject(existingObjectWithState: ObjectWithState, newObjectId: ObjectId): StackObject = StackObject(
    existingObjectWithState.gameObject.underlyingObject,
    newObjectId,
    player)

  override def addGameObjectToState(existingObjectWithState: ObjectWithState, gameObjectState: GameObjectState, objectConstructor: ObjectId => StackObject): GameObjectState = {
    gameObjectState.addObjectToStack(objectConstructor)
  }
}
