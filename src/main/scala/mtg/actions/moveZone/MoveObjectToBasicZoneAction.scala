package mtg.actions.moveZone

import mtg.definitions.ObjectId
import mtg.definitions.zones.Zone.BasicZone
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
