package mtg.game.state

import mtg.game.objects.GameObjectState
import mtg.game.state.history.LogEvent

case class GameObjectEventResult(newGameObjectState: Option[GameObjectState], childActions: Seq[GameObjectEvent]) extends GameActionResult {
  override def logEvent: Option[LogEvent] = None
}

object GameObjectEventResult {
  implicit def updatedGameObjectState(gameObjectState: GameObjectState): GameObjectEventResult = optionalUpdatedGameObjectState(Some(gameObjectState))
  implicit def optionalUpdatedGameObjectState(gameObjectStateOption: Option[GameObjectState]): GameObjectEventResult = GameObjectEventResult(gameObjectStateOption, Nil)
  implicit def childAction(action: GameObjectEvent): GameObjectEventResult = childActions(Seq(action))
  implicit def optionalChildAction(actionOption: Option[GameObjectEvent]): GameObjectEventResult = childActions(actionOption.toSeq)
  implicit def childActions(actions: Seq[GameObjectEvent]): GameObjectEventResult = GameObjectEventResult(None, actions)
  implicit def nothing(unit: Unit): GameObjectEventResult = GameObjectEventResult(None, Nil)
}
