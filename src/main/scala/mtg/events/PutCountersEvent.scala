package mtg.events

import mtg.game.ObjectId
import mtg.game.state.{InternalGameAction, GameActionResult, GameState}
import mtg.parts.counters.CounterType

case class PutCountersEvent(number: Int, kind: CounterType, objectId: ObjectId) extends InternalGameAction {
  override def execute(gameState: GameState): GameActionResult = {
    gameState.gameObjectState.allObjects.find(_.objectId == objectId)
      .map(_.updateCounters(gameState.gameObjectState, counters => counters.updatedWith(kind)(_.map(_ + number).orElse(Some(number)))))
  }
  override def canBeReverted: Boolean = true
}
