package mtg.actions.moveZone

import mtg.core.ObjectId
import mtg.core.zones.Zone.BasicZone
import mtg.game.objects.BasicGameObject
import mtg.game.state.ObjectWithState

abstract class MoveObjectToBasicZoneAction extends MoveObjectAction[BasicGameObject] {
  def getZone(existingObjectWithState: ObjectWithState): BasicZone
  override def createNewObject(existingObjectWithState: ObjectWithState, newObjectId: ObjectId): BasicGameObject = {
    BasicGameObject(
      existingObjectWithState.gameObject.underlyingObject,
      newObjectId,
      getZone(existingObjectWithState))
  }
}
