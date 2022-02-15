package mtg.events.moveZone

import mtg.game.objects.{BasicGameObject, GameObjectState}
import mtg.game.state.ObjectWithState
import mtg.game.{ObjectId, TypedZone, Zone}

case class MoveToGraveyardEvent(objectId: ObjectId) extends MoveObjectToSimpleZoneEvent {
  def getZone(existingObjectWithState: ObjectWithState): TypedZone[BasicGameObject] = Zone.Graveyard(existingObjectWithState.gameObject.owner)

  override def addGameObjectToState(existingObjectWithState: ObjectWithState, gameObjectState: GameObjectState, objectConstructor: ObjectId => BasicGameObject): GameObjectState = {
    gameObjectState.addObjectToGraveyard(existingObjectWithState.gameObject.owner, objectConstructor)
  }
}
