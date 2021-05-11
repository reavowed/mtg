package mtg.events

import mtg.game.objects.GameObjectState

sealed abstract class EventResult
object EventResult {
  case class UpdatedGameObjectState(gameObjectState: GameObjectState) extends EventResult
  case class SubEvents(events: Seq[Event]) extends EventResult
  case object Nothing extends EventResult

  implicit def updatedGameObjectState(gameObjectState: GameObjectState): EventResult = UpdatedGameObjectState(gameObjectState)
  implicit def subEvents(events: Seq[Event]): EventResult = SubEvents(events)
  implicit def nothing(unit: Unit): EventResult = Nothing
}
