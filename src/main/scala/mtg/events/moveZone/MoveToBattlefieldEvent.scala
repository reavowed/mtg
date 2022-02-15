package mtg.events.moveZone

import mtg.game.objects.{GameObjectState, PermanentObject}
import mtg.game.state.ObjectWithState
import mtg.game.{ObjectId, PlayerId}

case class MoveToBattlefieldEvent(objectId: ObjectId, initialController: PlayerId) extends MoveObjectEvent[PermanentObject] {
  override def createNewObject(existingObjectWithState: ObjectWithState, newObjectId: ObjectId): PermanentObject = PermanentObject(
    existingObjectWithState.gameObject.underlyingObject,
    newObjectId,
    initialController)

  override def addGameObjectToState(existingObjectWithState: ObjectWithState, gameObjectState: GameObjectState, objectConstructor: ObjectId => PermanentObject): GameObjectState = {
    gameObjectState.addObjectToBattlefield(objectConstructor)
  }
}
