package mtg.actions

import mtg.core.ObjectId
import mtg.game.state.{DirectGameObjectAction, GameState}
import mtg.parts.counters.CounterType

case class PutCountersAction(counters: Map[CounterType, Int], objectId: ObjectId) extends DirectGameObjectAction[Unit] {
  override def execute(implicit gameState: GameState): DirectGameObjectAction.Result[Unit] = {
    gameState.gameObjectState.updateObjectById(
      objectId,
      _.addCounters(counters))
  }
  override def canBeReverted: Boolean = true
}
