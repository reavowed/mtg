package mtg.events.shuffle

import mtg.events.MoveObjectEvent
import mtg.game.state.{GameActionResult, GameState, InternalGameAction}
import mtg.game.{PlayerId, Zone}

case class ShuffleHandIntoLibrary(player: PlayerId) extends InternalGameAction {
  override def execute(gameState: GameState): GameActionResult = {
    gameState.gameObjectState.hands(player)
      .map(MoveObjectEvent(player, _, Zone.Library(player))) :+ ShuffleLibrary(player)
  }
  override def canBeReverted: Boolean = true
}
