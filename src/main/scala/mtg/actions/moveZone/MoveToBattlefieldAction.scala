package mtg.actions.moveZone

import mtg.core.{ObjectId, PlayerId}
import mtg.game.objects.{GameObjectState, PermanentObject}
import mtg.game.state.ObjectWithState
import mtg.parts.counters.CounterType

case class MoveToBattlefieldAction(objectId: ObjectId, initialController: PlayerId, counters: Map[CounterType, Int]) extends MoveObjectAction[PermanentObject] {
  override def createNewObject(existingObjectWithState: ObjectWithState, newObjectId: ObjectId): PermanentObject = PermanentObject(
    existingObjectWithState.gameObject.underlyingObject,
    newObjectId,
    initialController,
    counters)

  override def addGameObjectToState(existingObjectWithState: ObjectWithState, gameObjectState: GameObjectState, objectConstructor: ObjectId => PermanentObject): (ObjectId, GameObjectState) = {
    gameObjectState.addObjectToBattlefield(objectConstructor)
  }
}

object MoveToBattlefieldAction {
  def apply(objectId: ObjectId, initialController: PlayerId): MoveToBattlefieldAction = {
    MoveToBattlefieldAction(objectId, initialController, Map.empty)
  }
}
