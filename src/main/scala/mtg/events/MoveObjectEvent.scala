package mtg.events

import mtg.game.{PlayerIdentifier, Zone}
import mtg.game.objects.{GameObject, ObjectId}
import mtg.game.state.{GameObjectEvent, GameObjectEventResult, GameState}

case class MoveObjectEvent(player: PlayerIdentifier, objectId: ObjectId, destination: Zone) extends GameObjectEvent {
  def execute(currentGameState: GameState): GameObjectEventResult = {
    currentGameState.gameObjectState.allObjects.find(_.objectId == objectId).map(gameObject => {
      val (newObject, intermediateState) = currentGameState.gameObjectState.createNewObjectForZone(gameObject, destination)
      val defaultController = if (destination == Zone.Stack) {
        // RULE 112.2 / Apr 22 2021 : A spell's controller is, by default, the player who put it on the stack.
        Some(player)
      } else if (destination == Zone.Battlefield) {
        if (gameObject.zone == Zone.Stack) {
          gameObject.defaultController
        } else {
          // RULE 110.2a / Apr 22 2021 : If an effect instructs a player to put an object onto the battlefield, that
          // object enters the battlefield under that player's control unless the effect states otherwise.
          // TODO: Handle putting something onto the battlefield under another player's control
          Some(player)
        }
      } else {
        None
      }
      intermediateState
        .updateZone(gameObject.zone, gameObjects => gameObjects.filter(_ != gameObject))
        .updateZone(destination, gameObjects => gameObjects :+ newObject.setDefaultController(defaultController))
    })
  }
}

object MoveObjectEvent {
  def apply(player: PlayerIdentifier, gameObject: GameObject, destination: Zone): MoveObjectEvent = {
    MoveObjectEvent(player, gameObject.objectId, destination)
  }
}
