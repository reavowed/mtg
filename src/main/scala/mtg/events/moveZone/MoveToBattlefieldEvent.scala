package mtg.events.moveZone

import mtg.game.objects.PermanentObject
import mtg.game.state.ObjectWithState
import mtg.game.{ObjectId, PlayerId}

case class MoveToBattlefieldEvent(objectId: ObjectId, player: PlayerId) extends MoveObjectEvent[PermanentObject] {
  override def createNewObject(existingObjectWithState: ObjectWithState, newObjectId: ObjectId): PermanentObject = PermanentObject(
    existingObjectWithState.gameObject.underlyingObject,
    newObjectId,
    player)
}
