package mtg.game.priority.actions

import mtg.core.PlayerId
import mtg.events.moveZone.MoveToBattlefieldEvent
import mtg.game.objects.GameObject
import mtg.game.state.{GameActionResult, GameState, InternalGameAction}

case class PlayLandEvent(player: PlayerId, landCard: GameObject) extends InternalGameAction {
  override def execute(gameState: GameState): GameActionResult = {
    MoveToBattlefieldEvent(landCard.objectId, player)
  }
  override def canBeReverted: Boolean = true
}
