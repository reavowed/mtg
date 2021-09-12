package mtg.events.shuffle

import mtg.game.PlayerId
import mtg.game.Zone.Library
import mtg.game.objects.BasicGameObject
import mtg.game.state.{GameObjectEvent, GameObjectEventResult, GameState}

import scala.util.Random

case class ShuffleLibrary(playerIdentifier: PlayerId) extends GameObjectEvent {
  override def execute(currentGameState: GameState): GameObjectEventResult = {
    val library = Library(playerIdentifier)
    val shuffledLibraryContents = Random.shuffle(library.getState(currentGameState))
    shuffledLibraryContents.foldLeft(
      library.updateState(currentGameState.gameObjectState, _ => Nil))(
      (gameState, oldObject) => gameState.addNewObject(BasicGameObject(oldObject.underlyingObject, _, library), _ => 0))
  }
  override def canBeReverted: Boolean = true
}
