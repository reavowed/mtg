package mtg.actions

import mtg.definitions.ObjectId
import mtg.game.state.{DirectGameObjectAction, GameState}

case class TapObjectAction(objectId: ObjectId) extends DirectGameObjectAction[Unit] {
  override def execute(implicit gameState: GameState): DirectGameObjectAction.Result[Unit] = {
    gameState.gameObjectState.updatePermanentObject(
      objectId,
      _.updatePermanentStatus(_.tap()))
  }
  override def canBeReverted: Boolean = true
}
