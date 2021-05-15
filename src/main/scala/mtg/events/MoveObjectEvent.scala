package mtg.events

import mtg.game.objects.{GameObject, GameObjectState}
import mtg.game.{GameData, Zone}

case class MoveObjectEvent(gameObject: GameObject, destination: Zone) extends Event {
  def execute(currentGameObjectState: GameObjectState, gameData: GameData): EventResult = {
    val (newObject, intermediateState) = currentGameObjectState.createNewObjectForZone(gameObject, destination)
    intermediateState
      .updateZone(gameObject.zone, gameObjects => gameObjects.filter(_ != gameObject))
      .updateZone(destination, gameObjects => newObject +: gameObjects)
  }
}
