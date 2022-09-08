package mtg.actions.stack

import mtg.definitions.ObjectId
import mtg.game.state.{DirectGameObjectAction, GameState}

case class SetMode(stackObjectId: ObjectId, modeIndex: Int) extends DirectGameObjectAction[Unit] {
  override def execute(implicit gameState: GameState): DirectGameObjectAction.Result[Unit] = {
    gameState.gameObjectState.updateStackObject(stackObjectId, _.addMode(modeIndex))
  }

  override def canBeReverted: Boolean = true
}
