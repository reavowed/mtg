package mtg.events

import mtg.game.GameState
import mtg.game.`object`.GameObject
import mtg.game.zone.Zone

case class MoveObjectEvent(gameObject: GameObject, destination: Zone) extends Event {
  override def execute(currentGameState: GameState): Either[GameState, Seq[Event]] = {
    val (newObject, intermediateState) = currentGameState.newObjectForZone(gameObject, destination)
    val resultState = intermediateState
      .updateZone(gameObject.zone, zoneState => zoneState.copy(objects = zoneState.objects.filter(_ != gameObject)))
      .updateZone(destination, zoneState => zoneState.copy(objects = zoneState.objects :+ newObject))
    Left(resultState)
  }
}
