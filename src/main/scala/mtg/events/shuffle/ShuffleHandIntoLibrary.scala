package mtg.events.shuffle

import mtg.events.moveZone.MoveToLibraryEvent
import mtg.game.PlayerId
import mtg.game.state.{GameActionResult, GameState, InternalGameAction}

case class ShuffleHandIntoLibrary(player: PlayerId) extends InternalGameAction {
  override def execute(gameState: GameState): GameActionResult = {
    gameState.gameObjectState.hands(player).map(obj => MoveToLibraryEvent(obj.objectId)) :+ ShuffleLibrary(player)
  }
  override def canBeReverted: Boolean = true
}
