package mtg.events

import mtg.game.GameData
import mtg.game.objects.{GameObject, GameObjectState}
import mtg.game.state.GameState
import mtg.game.zone.Zone

case class MoveObjectEvent(gameObject: GameObject, destination: Zone) extends Event {
  def execute(currentGameObjectState: GameObjectState, gameData: GameData): EventResult = {
    val (newObject, intermediateState) = currentGameObjectState.createNewObjectForZone(gameObject, destination)
    intermediateState
      .updateZone(gameObject.zone, zoneState => zoneState.copy(objects = zoneState.objects.filter(_ != gameObject)))
      .updateZone(destination, zoneState => zoneState.copy(objects = zoneState.objects :+ newObject))
  }
}
