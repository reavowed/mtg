package mtg.events

import mtg.game.{PlayerIdentifier, Zone}
import mtg.game.objects.GameObject
import mtg.game.state.{GameObjectEvent, GameObjectEventResult, GameState}

case class MoveObjectEvent(player: PlayerIdentifier, gameObject: GameObject, destination: Zone) extends GameObjectEvent {
  def execute(currentGameState: GameState): GameObjectEventResult = {
    val (newObject, intermediateState) = currentGameState.gameObjectState.createNewObjectForZone(gameObject, destination)
    val newObjectWithController =  newObject.setDefaultController(if (destination == Zone.Battlefield) Some(player) else None)
    intermediateState
      .updateZone(gameObject.zone, gameObjects => gameObjects.filter(_ != gameObject))
      .updateZone(destination, gameObjects => gameObjects :+ newObjectWithController)
  }
}
