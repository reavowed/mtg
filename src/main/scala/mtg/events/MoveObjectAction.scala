package mtg.events

import mtg.game.objects.GameObject
import mtg.game.state.{GameObjectAction, GameObjectActionResult, GameState}
import mtg.game.{ObjectId, PlayerId, Zone}

case class MoveObjectAction(player: PlayerId, objectId: ObjectId, destination: Zone) extends GameObjectAction {
  def execute(currentGameState: GameState): GameObjectActionResult = {
    currentGameState.gameObjectState.derivedState.allObjectStates.get(objectId).map(gameObjectWithState => {
        // TODO: Handle putting something onto the battlefield under another player's control
      currentGameState.gameObjectState.deleteObject(gameObjectWithState.gameObject)
        .addNewObject(destination.newObjectForZone(gameObjectWithState, player, _), _.length)
    })
  }
}

object MoveObjectAction {
  def apply(player: PlayerId, gameObject: GameObject, destination: Zone): MoveObjectAction = {
    MoveObjectAction(player, gameObject.objectId, destination)
  }
}
