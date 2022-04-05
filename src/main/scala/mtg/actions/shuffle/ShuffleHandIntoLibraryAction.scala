package mtg.actions.shuffle

import mtg.core.PlayerId
import mtg.actions.moveZone.MoveToLibraryAction
import mtg.game.state.{GameActionResult, GameState, GameObjectAction}

case class ShuffleHandIntoLibraryAction(player: PlayerId) extends GameObjectAction {
  override def execute(gameState: GameState): GameActionResult = {
    gameState.gameObjectState.hands(player).map(obj => MoveToLibraryAction(obj.objectId)) :+ ShuffleLibraryAction(player)
  }
  override def canBeReverted: Boolean = true
}
