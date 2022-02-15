package mtg.events.moveZone

import mtg.game.objects.StackObject
import mtg.game.state.ObjectWithState
import mtg.game.{ObjectId, PlayerId}

case class MoveToStackEvent(objectId: ObjectId, player: PlayerId) extends MoveObjectEvent[StackObject] {
  override def createNewObject(existingObjectWithState: ObjectWithState, newObjectId: ObjectId): StackObject = StackObject(
    existingObjectWithState.gameObject.underlyingObject,
    newObjectId,
    player)
}
