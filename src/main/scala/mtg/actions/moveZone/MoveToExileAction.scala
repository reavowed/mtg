package mtg.actions.moveZone

import mtg.definitions.ObjectId
import mtg.definitions.zones.Zone
import mtg.definitions.zones.Zone.BasicZone
import mtg.game.objects.{BasicGameObject, GameObjectState}
import mtg.game.state.ObjectWithState

case class MoveToExileAction(objectId: ObjectId) extends MoveObjectToBasicZoneAction {
  def getZone(existingObjectWithState: ObjectWithState): BasicZone = Zone.Exile

  override def addGameObjectToState(existingObjectWithState: ObjectWithState, gameObjectState: GameObjectState, objectConstructor: ObjectId => BasicGameObject): (ObjectId, GameObjectState) = {
    gameObjectState.addObjectToExile(objectConstructor)
  }
}
