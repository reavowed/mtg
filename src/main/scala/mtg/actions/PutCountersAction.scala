package mtg.actions

import mtg.core.ObjectId
import mtg.game.state.{GameActionResult, GameState, GameObjectAction}
import mtg.parts.counters.CounterType

case class PutCountersAction(number: Int, kind: CounterType, objectId: ObjectId) extends GameObjectAction {
  override def execute(gameState: GameState): GameActionResult = {
    gameState.gameObjectState.updateObjectById(objectId, _.updateCounters(_.updatedWith(kind)(_.map(_ + number).orElse(Some(number)))))
  }
  override def canBeReverted: Boolean = true
}
