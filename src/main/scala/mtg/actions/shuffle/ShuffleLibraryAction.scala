package mtg.actions.shuffle

import mtg.core.PlayerId
import mtg.core.zones.Zone
import mtg.game.objects.BasicGameObject
import mtg.game.state.{DirectGameObjectAction, GameState}

import scala.util.Random

case class ShuffleLibraryAction(player: PlayerId) extends DirectGameObjectAction[Unit] {
  override def execute(implicit gameState: GameState): DirectGameObjectAction.Result[Unit] = {
    val library = Zone.Library(player)
    val shuffledLibraryContents = Random.shuffle(gameState.gameObjectState.libraries(player))
    shuffledLibraryContents.foldLeft(
      gameState.gameObjectState.updateZone(library, _ => Nil))(
      (gameObjectState, oldObject) => gameObjectState.addObjectToLibrary(player, BasicGameObject(oldObject.underlyingObject, _, Zone.Library(player)), _ => 0)._2)
  }
  override def canBeReverted: Boolean = true
}
