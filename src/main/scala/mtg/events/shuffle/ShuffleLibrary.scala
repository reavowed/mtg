package mtg.events.shuffle

import mtg.game.PlayerId
import mtg.game.Zone.Library
import mtg.game.objects.BasicGameObject
import mtg.game.state.{GameActionResult, GameState, InternalGameAction}

import scala.util.Random

case class ShuffleLibrary(playerIdentifier: PlayerId) extends InternalGameAction {
  override def execute(gameState: GameState): GameActionResult = {
    val library = Library(playerIdentifier)
    val shuffledLibraryContents = Random.shuffle(gameState.gameObjectState.getZoneState(library))
    shuffledLibraryContents.foldLeft(
      gameState.gameObjectState.updateZoneState(library)(_ => Nil))(
      (gameState, oldObject) => gameState.createObject(BasicGameObject(oldObject.underlyingObject, _, library), _ => 0))
  }
  override def canBeReverted: Boolean = true
}
