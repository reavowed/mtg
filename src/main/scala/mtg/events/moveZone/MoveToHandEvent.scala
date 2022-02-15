package mtg.events.moveZone

import mtg.game.{ObjectId, TypedZone, Zone}
import mtg.game.objects.{BasicGameObject, GameObjectState}
import mtg.game.state.ObjectWithState

case class MoveToHandEvent(objectId: ObjectId) extends MoveObjectToSimpleZoneEvent {
  def getZone(existingObjectWithState: ObjectWithState): TypedZone[BasicGameObject] = Zone.Hand(existingObjectWithState.gameObject.owner)

  override def addGameObjectToState(existingObjectWithState: ObjectWithState, gameObjectState: GameObjectState, objectConstructor: ObjectId => BasicGameObject): GameObjectState = {
    gameObjectState.addObjectToHand(existingObjectWithState.gameObject.owner, objectConstructor)
  }
}
