package mtg.actions.moveZone

import mtg.definitions.ObjectId
import mtg.definitions.zones.Zone
import mtg.definitions.zones.Zone.BasicZone
import mtg.game.objects.{BasicGameObject, GameObjectState}
import mtg.game.state.ObjectWithState

// TODO: Move to set location in library
case class MoveToLibraryAction(objectId: ObjectId) extends MoveObjectToBasicZoneAction {
  def getZone(existingObjectWithState: ObjectWithState): BasicZone = Zone.Library(existingObjectWithState.gameObject.owner)

  override def addGameObjectToState(existingObjectWithState: ObjectWithState, gameObjectState: GameObjectState, objectConstructor: ObjectId => BasicGameObject): (ObjectId, GameObjectState) = {
    gameObjectState.addObjectToLibrary(existingObjectWithState.gameObject.owner, objectConstructor, _.length)
  }
}
