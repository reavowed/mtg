package mtg.events.shuffle

import mtg.game.PlayerIdentifier
import mtg.game.Zone.Library
import mtg.game.objects.GameObject
import mtg.game.state.{GameObjectEvent, GameObjectEventResult, GameState}

import scala.util.Random

case class ShuffleLibrary(playerIdentifier: PlayerIdentifier) extends GameObjectEvent {
  override def execute(currentGameState: GameState): GameObjectEventResult = {
    val library = Library(playerIdentifier)
    val (intermediateState, newObjects) = currentGameState.gameObjectState.libraries(playerIdentifier)
      .foldLeft((currentGameState.gameObjectState, Seq.empty[GameObject])) {
        case ((state, newObjects), currentObject) =>
          val (newObject, newState) = state.createNewObjectForZone(currentObject, library)
          (newState, newObjects :+ newObject)
      }
    intermediateState.updateZone(library, _ => Random.shuffle(newObjects))
  }
}
