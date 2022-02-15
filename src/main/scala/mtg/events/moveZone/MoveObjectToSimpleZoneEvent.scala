package mtg.events.moveZone

import mtg.game.objects.BasicGameObject
import mtg.game.state.ObjectWithState
import mtg.game.{ObjectId, TypedZone}

abstract class MoveObjectToSimpleZoneEvent extends MoveObjectEvent[BasicGameObject] {
  def getZone(existingObjectWithState: ObjectWithState): TypedZone[BasicGameObject]
  override def createNewObject(existingObjectWithState: ObjectWithState, newObjectId: ObjectId): BasicGameObject = {
    BasicGameObject(
      existingObjectWithState.gameObject.underlyingObject,
      newObjectId,
      getZone(existingObjectWithState))
  }
}
