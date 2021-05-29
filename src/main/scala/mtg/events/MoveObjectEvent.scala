package mtg.events

import mtg.game.objects.GameObject
import mtg.game.state.{GameObjectEvent, GameObjectEventResult, GameState}
import mtg.game.{ObjectId, PlayerId, Zone}

case class MoveObjectEvent(player: PlayerId, objectId: ObjectId, destination: Zone) extends GameObjectEvent {
  def execute(currentGameState: GameState): GameObjectEventResult = {
    currentGameState.gameObjectState.derivedState.allObjectStates.get(objectId).map(gameObjectWithState => {
        // TODO: Handle putting something onto the battlefield under another player's control
      gameObjectWithState.gameObject
        .removeFromCurrentZone(currentGameState.gameObjectState)
        .addNewObject(destination.newObjectForZone(gameObjectWithState, player, _), _.length)
    })
  }
}

object MoveObjectEvent {
  def apply(player: PlayerId, gameObject: GameObject, destination: Zone): MoveObjectEvent = {
    MoveObjectEvent(player, gameObject.objectId, destination)
  }
}
