package mtg.game.state

import mtg.game.objects.GameObjectState

sealed abstract class GameObjectEventResult
object GameObjectEventResult {
  case class UpdatedGameObjectState(gameObjectState: GameObjectState) extends GameObjectEventResult
  case class SubEvents(events: Seq[GameObjectEvent]) extends GameObjectEventResult
  case object Nothing extends GameObjectEventResult

  implicit def updatedGameObjectState(gameObjectState: GameObjectState): GameObjectEventResult = UpdatedGameObjectState(gameObjectState)
  implicit def subEvents(events: Seq[GameObjectEvent]): GameObjectEventResult = SubEvents(events)
  implicit def nothing(unit: Unit): GameObjectEventResult = Nothing
}
