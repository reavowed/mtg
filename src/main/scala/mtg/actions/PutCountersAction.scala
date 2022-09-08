package mtg.actions

import mtg.definitions.ObjectId
import mtg.game.state.{DirectGameObjectAction, GameState}
import mtg.parts.Counter

case class PutCountersAction(counters: Map[Counter, Int], objectId: ObjectId) extends DirectGameObjectAction[Unit] {
  override def execute(implicit gameState: GameState): DirectGameObjectAction.Result[Unit] = {
    gameState.gameObjectState.updateObjectById(
      objectId,
      _.addCounters(counters))
  }
  override def canBeReverted: Boolean = true
}
