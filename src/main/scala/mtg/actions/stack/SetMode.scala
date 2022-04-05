package mtg.actions.stack

import mtg.core.ObjectId
import mtg.game.state.{GameActionResult, GameState, InternalGameAction}

case class SetMode(stackObjectId: ObjectId, modeIndex: Int) extends InternalGameAction {
  override def execute(gameState: GameState): GameActionResult = {
    gameState.gameObjectState.updateStackObject(stackObjectId, _.addMode(modeIndex))
  }

  override def canBeReverted: Boolean = true
}
