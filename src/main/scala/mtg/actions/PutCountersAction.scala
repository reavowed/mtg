package mtg.actions

import mtg.core.ObjectId
import mtg.game.objects.GameObjectState
import mtg.game.state.{DirectGameObjectAction, GameState}
import mtg.parts.counters.{CounterSpecification, CounterType}

case class PutCountersAction(counterSpecification: CounterSpecification, objectId: ObjectId) extends DirectGameObjectAction[Unit] {
  override def execute(implicit gameState: GameState): DirectGameObjectAction.Result[Unit] = {
    gameState.gameObjectState.updateObjectById(
      objectId,
      _.updateCounters(counterSpecification.addToMap(_)))
  }
  override def canBeReverted: Boolean = true
}
