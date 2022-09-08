package mtg.actions.moveZone

import mtg.definitions.ObjectId
import mtg.definitions.zones.Zone
import mtg.definitions.zones.Zone.BasicZone
import mtg.game.objects.{BasicGameObject, GameObjectState}
import mtg.game.state.ObjectWithState

case class MoveToHandAction(objectId: ObjectId) extends MoveObjectToBasicZoneAction {
  def getZone(existingObjectWithState: ObjectWithState): BasicZone = Zone.Hand(existingObjectWithState.gameObject.owner)

  override def addGameObjectToState(existingObjectWithState: ObjectWithState, gameObjectState: GameObjectState, objectConstructor: ObjectId => BasicGameObject): (ObjectId, GameObjectState) = {
    gameObjectState.addObjectToHand(existingObjectWithState.gameObject.owner, objectConstructor)
  }
}
