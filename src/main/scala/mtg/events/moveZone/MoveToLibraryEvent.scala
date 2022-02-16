package mtg.events.moveZone

import mtg.core.ObjectId
import mtg.core.zones.Zone
import mtg.core.zones.Zone.BasicZone
import mtg.game.objects.{BasicGameObject, GameObjectState}
import mtg.game.state.ObjectWithState

// TODO: Move to set location in library
case class MoveToLibraryEvent(objectId: ObjectId) extends MoveObjectToBasicZoneEvent {
  def getZone(existingObjectWithState: ObjectWithState): BasicZone = Zone.Library(existingObjectWithState.gameObject.owner)

  override def addGameObjectToState(existingObjectWithState: ObjectWithState, gameObjectState: GameObjectState, objectConstructor: ObjectId => BasicGameObject): GameObjectState = {
    gameObjectState.addObjectToLibrary(existingObjectWithState.gameObject.owner, objectConstructor, _.length)
  }
}
