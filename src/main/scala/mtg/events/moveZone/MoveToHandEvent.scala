package mtg.events.moveZone

import mtg.game.Zone.BasicZone
import mtg.game.objects.{BasicGameObject, GameObjectState}
import mtg.game.state.ObjectWithState
import mtg.game.{ObjectId, Zone}

case class MoveToHandEvent(objectId: ObjectId) extends MoveObjectToSimpleZoneEvent {
  def getZone(existingObjectWithState: ObjectWithState): BasicZone = Zone.Hand(existingObjectWithState.gameObject.owner)

  override def addGameObjectToState(existingObjectWithState: ObjectWithState, gameObjectState: GameObjectState, objectConstructor: ObjectId => BasicGameObject): GameObjectState = {
    gameObjectState.addObjectToHand(existingObjectWithState.gameObject.owner, objectConstructor)
  }
}
