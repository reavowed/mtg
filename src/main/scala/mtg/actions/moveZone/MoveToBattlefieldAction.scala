package mtg.actions.moveZone

import mtg.core.{ObjectId, PlayerId}
import mtg.game.objects.{GameObjectState, PermanentObject}
import mtg.game.state.ObjectWithState

case class MoveToBattlefieldAction(objectId: ObjectId, initialController: PlayerId) extends MoveObjectAction[PermanentObject] {
  override def createNewObject(existingObjectWithState: ObjectWithState, newObjectId: ObjectId): PermanentObject = PermanentObject(
    existingObjectWithState.gameObject.underlyingObject,
    newObjectId,
    initialController)

  override def addGameObjectToState(existingObjectWithState: ObjectWithState, gameObjectState: GameObjectState, objectConstructor: ObjectId => PermanentObject): GameObjectState = {
    gameObjectState.addObjectToBattlefield(objectConstructor)
  }
}
