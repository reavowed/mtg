package mtg.actions

import mtg.core.ObjectId
import mtg.actions.moveZone.MoveToGraveyardAction
import mtg.game.state.{GameActionResult, GameState, InternalGameAction}

case class DestroyAction(objectId: ObjectId) extends InternalGameAction {
  override def execute(gameState: GameState): GameActionResult = {
    MoveToGraveyardAction(objectId)
  }
  override def canBeReverted: Boolean = true
}
