package mtg.events

import mtg.events.moveZone.MoveToGraveyardEvent
import mtg.game.ObjectId
import mtg.game.state.{GameActionResult, GameState, InternalGameAction}

case class DestroyEvent(objectId: ObjectId) extends InternalGameAction {
  override def execute(gameState: GameState): GameActionResult = {
    MoveToGraveyardEvent(objectId)
  }
  override def canBeReverted: Boolean = true
}
