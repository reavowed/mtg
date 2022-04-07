package mtg.actions

import mtg.core.ObjectId
import mtg.game.objects.GameObjectState
import mtg.game.state.{DirectGameObjectAction, GameState}
import mtg.parts.counters.CounterType

case class PutCountersAction(number: Int, kind: CounterType, objectId: ObjectId) extends DirectGameObjectAction {
  override def execute(implicit gameState: GameState): GameObjectState = {
    gameState.gameObjectState.updateObjectById(objectId, _.updateCounters(_.updatedWith(kind)(_.map(_ + number).orElse(Some(number)))))
  }
  override def canBeReverted: Boolean = true
}
