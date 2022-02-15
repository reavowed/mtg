package mtg.events.moveZone

import mtg.game.objects.BasicGameObject
import mtg.game.state.ObjectWithState
import mtg.game.{ObjectId, TypedZone, Zone}

case class MoveToExileEvent(objectId: ObjectId) extends MoveObjectToSimpleZoneEvent {
  def getZone(existingObjectWithState: ObjectWithState): TypedZone[BasicGameObject] = Zone.Exile
}
