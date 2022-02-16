package mtg.events.moveZone

import mtg.core.ObjectId
import mtg.game.Zone.BasicZone
import mtg.game.objects.{BasicGameObject, GameObjectState}
import mtg.game.state.ObjectWithState
import mtg.game.Zone

case class MoveToGraveyardEvent(objectId: ObjectId) extends MoveObjectToSimpleZoneEvent {
  def getZone(existingObjectWithState: ObjectWithState): BasicZone = Zone.Graveyard(existingObjectWithState.gameObject.owner)

  override def addGameObjectToState(existingObjectWithState: ObjectWithState, gameObjectState: GameObjectState, objectConstructor: ObjectId => BasicGameObject): GameObjectState = {
    gameObjectState.addObjectToGraveyard(existingObjectWithState.gameObject.owner, objectConstructor)
  }
}
