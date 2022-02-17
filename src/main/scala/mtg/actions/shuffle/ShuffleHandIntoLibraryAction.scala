package mtg.actions.shuffle

import mtg.core.PlayerId
import mtg.actions.moveZone.MoveToLibraryAction
import mtg.game.state.{GameActionResult, GameState, InternalGameAction}

case class ShuffleHandIntoLibraryAction(player: PlayerId) extends InternalGameAction {
  override def execute(gameState: GameState): GameActionResult = {
    gameState.gameObjectState.hands(player).map(obj => MoveToLibraryAction(obj.objectId)) :+ ShuffleLibraryAction(player)
  }
  override def canBeReverted: Boolean = true
}
