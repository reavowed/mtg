package mtg.game.actions

import mtg.events.MoveObjectEvent
import mtg.game.objects.GameObject
import mtg.game.state.{GameActionResult, GameState, InternalGameAction}
import mtg.game.{PlayerId, Zone}

case class PlayLandEvent(player: PlayerId, landCard: GameObject) extends InternalGameAction {
  override def execute(gameState: GameState): GameActionResult = {
    MoveObjectEvent(player, landCard, Zone.Battlefield)
  }
  override def canBeReverted: Boolean = true
}
