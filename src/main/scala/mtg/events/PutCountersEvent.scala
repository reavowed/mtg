package mtg.events

import mtg.game.ObjectId
import mtg.game.state.{GameActionResult, GameState, InternalGameAction}
import mtg.parts.counters.CounterType

case class PutCountersEvent(number: Int, kind: CounterType, objectId: ObjectId) extends InternalGameAction {
  override def execute(gameState: GameState): GameActionResult = {
    gameState.gameObjectState.updateObjectById(objectId, _.updateCounters(_.updatedWith(kind)(_.map(_ + number).orElse(Some(number)))))
  }
  override def canBeReverted: Boolean = true
}
