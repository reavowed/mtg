package mtg.events.moveZone

import mtg.game.objects.BasicGameObject
import mtg.game.state.ObjectWithState
import mtg.game.{ObjectId, TypedZone, Zone}

case class MoveToLibraryEvent(objectId: ObjectId) extends MoveObjectToSimpleZoneEvent {
  def getZone(existingObjectWithState: ObjectWithState): TypedZone[BasicGameObject] = Zone.Library(existingObjectWithState.gameObject.owner)
}
