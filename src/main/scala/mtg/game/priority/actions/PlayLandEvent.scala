package mtg.game.priority.actions

import mtg.core.PlayerId
import mtg.actions.moveZone.MoveToBattlefieldAction
import mtg.game.objects.GameObject
import mtg.game.state.{GameActionResult, GameState, InternalGameAction}

case class PlayLandEvent(player: PlayerId, landCard: GameObject) extends InternalGameAction {
  override def execute(gameState: GameState): GameActionResult = {
    MoveToBattlefieldAction(landCard.objectId, player)
  }
  override def canBeReverted: Boolean = true
}
