package mtg.events

import mtg.core.ObjectId
import mtg.game.state.{GameActionResult, GameState, InternalGameAction}

case class TapObjectEvent(objectId: ObjectId) extends InternalGameAction {
  override def execute(gameState: GameState): GameActionResult = {
    gameState.gameObjectState.updatePermanentObject(
      objectId,
      _.updatePermanentStatus(_.tap()))
  }
  override def canBeReverted: Boolean = true
}
