package mtg.events

import mtg.game.objects.GameObject
import mtg.game.state.{GameObjectEvent, GameObjectEventResult, GameState}
import mtg.game.{ObjectId, PlayerId, Zone}

case class MoveObjectEvent(player: PlayerId, objectId: ObjectId, destination: Zone) extends GameObjectEvent {
  def execute(currentGameState: GameState): GameObjectEventResult = {
    currentGameState.gameObjectState.derivedState.allObjectStates.get(objectId).map(gameObjectWithState => {
        // TODO: Handle putting something onto the battlefield under another player's control
      currentGameState.gameObjectState.deleteObject(gameObjectWithState.gameObject)
        .addNewObject(destination.newObjectForZone(gameObjectWithState, player, _), _.length)
    })
  }
  // TODO: figure out how to make this revertible if the previous card was not hidden
  override def canBeReverted: Boolean = false
}

object MoveObjectEvent {
  def apply(player: PlayerId, gameObject: GameObject, destination: Zone): MoveObjectEvent = {
    MoveObjectEvent(player, gameObject.objectId, destination)
  }
}
