package mtg.events.shuffle

import mtg.game.PlayerIdentifier
import mtg.game.Zone.Library
import mtg.game.state.{GameObjectEvent, GameObjectEventResult, GameState}

import scala.util.Random

case class ShuffleLibrary(playerIdentifier: PlayerIdentifier) extends GameObjectEvent {
  override def execute(currentGameState: GameState): GameObjectEventResult = {
    val library = Library(playerIdentifier)
    val shuffledLibraryContents = Random.shuffle(library.getState(currentGameState))
    shuffledLibraryContents.foldLeft(
      currentGameState.gameObjectState.updateZone(library, _ => Nil))(
      (gameState, oldObject) => gameState.addObject(library, oldObject.forNewZone(_, library, None), _ => 0))
  }
}
