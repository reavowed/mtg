package mtg.events.moveZone

import mtg.core.ObjectId
import mtg.game.Zone.BasicZone
import mtg.game.objects.{BasicGameObject, GameObjectState}
import mtg.game.state.ObjectWithState
import mtg.game.Zone

case class MoveToExileEvent(objectId: ObjectId) extends MoveObjectToSimpleZoneEvent {
  def getZone(existingObjectWithState: ObjectWithState): BasicZone = Zone.Exile

  override def addGameObjectToState(existingObjectWithState: ObjectWithState, gameObjectState: GameObjectState, objectConstructor: ObjectId => BasicGameObject): GameObjectState = {
    gameObjectState.addObjectToExile(objectConstructor)
  }
}
