package mtg.events.shuffle

import mtg.events.{Event, EventResult}
import mtg.game.Zone.Library
import mtg.game.objects.{GameObject, GameObjectState}
import mtg.game.{GameData, PlayerIdentifier}

import scala.util.Random

case class ShuffleLibrary(playerIdentifier: PlayerIdentifier) extends Event {
  override def execute(currentGameObjectState: GameObjectState, gameData: GameData): EventResult = {
    val library = Library(playerIdentifier)
    val (intermediateState, newObjects) = currentGameObjectState.libraries(playerIdentifier)
      .foldLeft((currentGameObjectState, Seq.empty[GameObject])) {
        case ((state, newObjects), currentObject) =>
          val (newObject, newState) = state.createNewObjectForZone(currentObject, library)
          (newState, newObjects :+ newObject)
      }
    intermediateState.updateZone(library, _ => Random.shuffle(newObjects))
  }
}
