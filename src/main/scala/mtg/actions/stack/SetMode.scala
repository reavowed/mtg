package mtg.actions.stack

import mtg.core.ObjectId
import mtg.game.objects.GameObjectState
import mtg.game.state.{DirectGameObjectAction, GameState}

case class SetMode(stackObjectId: ObjectId, modeIndex: Int) extends DirectGameObjectAction {
  override def execute(implicit gameState: GameState): GameObjectState = {
    gameState.gameObjectState.updateStackObject(stackObjectId, _.addMode(modeIndex))
  }

  override def canBeReverted: Boolean = true
}
