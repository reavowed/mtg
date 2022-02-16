package mtg.events.moveZone

import mtg.core.ObjectId
import mtg.game.Zone.BasicZone
import mtg.game.objects.BasicGameObject
import mtg.game.state.ObjectWithState

abstract class MoveObjectToSimpleZoneEvent extends MoveObjectEvent[BasicGameObject] {
  def getZone(existingObjectWithState: ObjectWithState): BasicZone
  override def createNewObject(existingObjectWithState: ObjectWithState, newObjectId: ObjectId): BasicGameObject = {
    BasicGameObject(
      existingObjectWithState.gameObject.underlyingObject,
      newObjectId,
      getZone(existingObjectWithState))
  }
}
