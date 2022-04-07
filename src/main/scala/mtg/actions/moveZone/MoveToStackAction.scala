package mtg.actions.moveZone

import mtg.core.{ObjectId, PlayerId}
import mtg.game.objects.{GameObjectState, StackObject}
import mtg.game.state.ObjectWithState

case class MoveToStackAction(objectId: ObjectId, player: PlayerId) extends MoveObjectAction[StackObject] {
  override def createNewObject(existingObjectWithState: ObjectWithState, newObjectId: ObjectId): StackObject = StackObject(
    existingObjectWithState.gameObject.underlyingObject,
    newObjectId,
    player)

  override def addGameObjectToState(existingObjectWithState: ObjectWithState, gameObjectState: GameObjectState, objectConstructor: ObjectId => StackObject): (ObjectId, GameObjectState) = {
    gameObjectState.addObjectToStack(objectConstructor)
  }
}
