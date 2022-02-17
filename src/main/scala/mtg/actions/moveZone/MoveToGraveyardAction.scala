package mtg.actions.moveZone

import mtg.core.ObjectId
import mtg.core.zones.Zone
import mtg.core.zones.Zone.BasicZone
import mtg.game.objects.{BasicGameObject, GameObjectState}
import mtg.game.state.ObjectWithState

case class MoveToGraveyardAction(objectId: ObjectId) extends MoveObjectToBasicZoneAction {
  def getZone(existingObjectWithState: ObjectWithState): BasicZone = Zone.Graveyard(existingObjectWithState.gameObject.owner)

  override def addGameObjectToState(existingObjectWithState: ObjectWithState, gameObjectState: GameObjectState, objectConstructor: ObjectId => BasicGameObject): GameObjectState = {
    gameObjectState.addObjectToGraveyard(existingObjectWithState.gameObject.owner, objectConstructor)
  }
}
