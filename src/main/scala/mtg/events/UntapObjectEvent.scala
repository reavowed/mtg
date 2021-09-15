package mtg.events

import mtg.game.ObjectId
import mtg.game.state.{InternalGameAction, GameActionResult, GameState}

case class UntapObjectEvent(objectId: ObjectId) extends InternalGameAction {
  override def execute(gameState: GameState): GameActionResult = {
    gameState.gameObjectState.updatePermanentObject(
      objectId,
      _.updatePermanentStatus(_.untap()))
  }
  override def canBeReverted: Boolean = true
}
