package mtg.actions.shuffle

import mtg.core.PlayerId
import mtg.core.zones.Zone
import mtg.game.objects.{BasicGameObject, GameObjectState}
import mtg.game.state.{DirectGameObjectAction, GameState}

import scala.util.Random

case class ShuffleLibraryAction(player: PlayerId) extends DirectGameObjectAction {
  override def execute(implicit gameState: GameState): GameObjectState = {
    val library = Zone.Library(player)
    val shuffledLibraryContents = Random.shuffle(gameState.gameObjectState.libraries(player))
    shuffledLibraryContents.foldLeft(
      gameState.gameObjectState.updateZone(library, _ => Nil))(
      (gameObjectState, oldObject) => gameObjectState.addObjectToLibrary(player, BasicGameObject(oldObject.underlyingObject, _, Zone.Library(player)), _ => 0))
  }
  override def canBeReverted: Boolean = true
}
