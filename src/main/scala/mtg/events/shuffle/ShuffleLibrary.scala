package mtg.events.shuffle

import mtg.game.{PlayerId, Zone}
import mtg.game.Zone.Library
import mtg.game.objects.BasicGameObject
import mtg.game.state.{GameActionResult, GameState, InternalGameAction}

import scala.util.Random

case class ShuffleLibrary(player: PlayerId) extends InternalGameAction {
  override def execute(gameState: GameState): GameActionResult = {
    val library = Library(player)
    val shuffledLibraryContents = Random.shuffle(gameState.gameObjectState.libraries(player))
    shuffledLibraryContents.foldLeft(
      gameState.gameObjectState.updateZoneState(library)(_ => Nil))(
      (gameObjectState, oldObject) => gameObjectState.addObjectToLibrary(player, BasicGameObject(oldObject.underlyingObject, _, Zone.Library(player)), _ => 0))
  }
  override def canBeReverted: Boolean = true
}
