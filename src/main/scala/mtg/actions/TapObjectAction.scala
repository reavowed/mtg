package mtg.actions

import mtg.core.ObjectId
import mtg.game.state.{GameActionResult, GameState, GameObjectAction}

case class TapObjectAction(objectId: ObjectId) extends GameObjectAction {
  override def execute(gameState: GameState): GameActionResult = {
    gameState.gameObjectState.updatePermanentObject(
      objectId,
      _.updatePermanentStatus(_.tap()))
  }
  override def canBeReverted: Boolean = true
}
