package mtg.events

import mtg.game.Zone
import mtg.game.objects.GameObject
import mtg.game.state.{GameObjectEvent, GameObjectEventResult, GameState}

case class MoveObjectEvent(gameObject: GameObject, destination: Zone) extends GameObjectEvent {
  def execute(currentGameState: GameState): GameObjectEventResult = {
    val (newObject, intermediateState) = currentGameState.gameObjectState.createNewObjectForZone(gameObject, destination)
    intermediateState
      .updateZone(gameObject.zone, gameObjects => gameObjects.filter(_ != gameObject))
      .updateZone(destination, gameObjects => gameObjects :+ newObject)
  }
}
