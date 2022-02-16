package mtg.events.moveZone

import mtg.core.ObjectId
import mtg.core.zones.Zone
import mtg.core.zones.Zone.BasicZone
import mtg.game.objects.{BasicGameObject, GameObjectState}
import mtg.game.state.ObjectWithState

case class MoveToHandEvent(objectId: ObjectId) extends MoveObjectToBasicZoneEvent {
  def getZone(existingObjectWithState: ObjectWithState): BasicZone = Zone.Hand(existingObjectWithState.gameObject.owner)

  override def addGameObjectToState(existingObjectWithState: ObjectWithState, gameObjectState: GameObjectState, objectConstructor: ObjectId => BasicGameObject): GameObjectState = {
    gameObjectState.addObjectToHand(existingObjectWithState.gameObject.owner, objectConstructor)
  }
}
