package mtg.actions

import mtg.core.ObjectId
import mtg.actions.moveZone.MoveToGraveyardEvent
import mtg.game.state.{GameActionResult, GameState, InternalGameAction}

case class DestroyEvent(objectId: ObjectId) extends InternalGameAction {
  override def execute(gameState: GameState): GameActionResult = {
    MoveToGraveyardEvent(objectId)
  }
  override def canBeReverted: Boolean = true
}
