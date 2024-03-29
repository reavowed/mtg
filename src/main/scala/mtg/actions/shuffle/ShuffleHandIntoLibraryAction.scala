package mtg.actions.shuffle

import mtg.actions.moveZone.MoveToLibraryAction
import mtg.definitions.PlayerId
import mtg.game.state.{DelegatingGameObjectAction, GameObjectAction, GameState}

case class ShuffleHandIntoLibraryAction(player: PlayerId) extends DelegatingGameObjectAction {
  override def delegate(implicit gameState: GameState): Seq[GameObjectAction[_]] = {
    gameState.gameObjectState.hands(player).map(obj => MoveToLibraryAction(obj.objectId)) :+ ShuffleLibraryAction(player)
  }
}
