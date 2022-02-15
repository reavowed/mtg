package mtg.events.moveZone

import mtg.game.{ObjectId, TypedZone, Zone}
import mtg.game.objects.BasicGameObject
import mtg.game.state.ObjectWithState

case class MoveToHandEvent(objectId: ObjectId) extends MoveObjectToSimpleZoneEvent {
  def getZone(existingObjectWithState: ObjectWithState): TypedZone[BasicGameObject] = Zone.Hand(existingObjectWithState.gameObject.owner)
}
