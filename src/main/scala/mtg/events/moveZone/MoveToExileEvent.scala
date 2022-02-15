package mtg.events.moveZone

import mtg.game.objects.{BasicGameObject, GameObjectState}
import mtg.game.state.ObjectWithState
import mtg.game.{ObjectId, TypedZone, Zone}

case class MoveToExileEvent(objectId: ObjectId) extends MoveObjectToSimpleZoneEvent {
  def getZone(existingObjectWithState: ObjectWithState): TypedZone[BasicGameObject] = Zone.Exile

  override def addGameObjectToState(existingObjectWithState: ObjectWithState, gameObjectState: GameObjectState, objectConstructor: ObjectId => BasicGameObject): GameObjectState = {
    gameObjectState.addObjectToExile(objectConstructor)
  }
}
