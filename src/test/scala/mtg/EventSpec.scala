package mtg

import mtg.game.objects.GameObjectState
import mtg.game.state.{GameObjectEvent, GameObjectEventResult}

abstract class EventSpec extends SpecWithGameObjectState {
  def checkResultAndGetGameObjectState(eventResult: GameObjectEventResult): GameObjectState = {
      eventResult must beAnInstanceOf[GameObjectEventResult.UpdatedGameObjectState]
      eventResult.asInstanceOf[GameObjectEventResult.UpdatedGameObjectState].gameObjectState
  }
  def checkResultAndGetEvents(eventResult: GameObjectEventResult): Seq[GameObjectEvent] = {
      eventResult must beAnInstanceOf[GameObjectEventResult.SubEvents]
      eventResult.asInstanceOf[GameObjectEventResult.SubEvents].events
  }
  def checkResultIsNothing(eventResult: GameObjectEventResult) = {
    eventResult mustEqual GameObjectEventResult.Nothing
  }
}
