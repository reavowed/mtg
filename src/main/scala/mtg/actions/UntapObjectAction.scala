package mtg.actions

import mtg.core.ObjectId
import mtg.game.state.{GameActionResult, GameState, GameObjectAction}

case class UntapObjectAction(objectId: ObjectId) extends GameObjectAction {
  override def execute(gameState: GameState): GameActionResult = {
    gameState.gameObjectState.updatePermanentObject(
      objectId,
      _.updatePermanentStatus(_.untap()))
  }
  override def canBeReverted: Boolean = true
}
