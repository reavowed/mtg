package mtg.events

import mtg.parts.counters.CounterType
import mtg.game.ObjectId
import mtg.game.state.{GameObjectAction, GameObjectActionResult, GameState}

case class PutCountersAction(number: Int, kind: CounterType, objectId: ObjectId) extends GameObjectAction {
  override def execute(currentGameState: GameState): GameObjectActionResult = {
    currentGameState.gameObjectState.allObjects.find(_.objectId == objectId)
      .map(_.updateCounters(currentGameState.gameObjectState, counters => counters.updatedWith(kind)(_.map(_ + number).orElse(Some(number)))))
  }
}
