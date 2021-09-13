package mtg.events

import mtg.game.ObjectId
import mtg.game.state.{GameObjectEvent, GameObjectEventResult, GameState}
import mtg.parts.counters.CounterType

case class PutCountersEvent(number: Int, kind: CounterType, objectId: ObjectId) extends GameObjectEvent {
  override def execute(currentGameState: GameState): GameObjectEventResult = {
    currentGameState.gameObjectState.allObjects.find(_.objectId == objectId)
      .map(_.updateCounters(currentGameState.gameObjectState, counters => counters.updatedWith(kind)(_.map(_ + number).orElse(Some(number)))))
  }
  override def canBeReverted: Boolean = true
}
