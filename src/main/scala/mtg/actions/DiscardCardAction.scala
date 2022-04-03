package mtg.actions

import mtg.actions.moveZone.MoveToGraveyardAction
import mtg.core.{ObjectId, PlayerId}
import mtg.game.state.{GameActionResult, GameState, InternalGameAction}

case class DiscardCardAction(player: PlayerId, objectId: ObjectId) extends InternalGameAction {
  def execute(gameState: GameState): GameActionResult = {
    MoveToGraveyardAction(objectId)
  }
  override def canBeReverted: Boolean = false
}
