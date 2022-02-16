package mtg.events

import mtg.core.ObjectId
import mtg.events.moveZone.MoveToGraveyardEvent
import mtg.game.state.{GameActionResult, GameState, InternalGameAction}

case class DestroyEvent(objectId: ObjectId) extends InternalGameAction {
  override def execute(gameState: GameState): GameActionResult = {
    MoveToGraveyardEvent(objectId)
  }
  override def canBeReverted: Boolean = true
}
