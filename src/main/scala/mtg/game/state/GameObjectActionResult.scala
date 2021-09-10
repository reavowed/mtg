package mtg.game.state

import mtg.game.objects.GameObjectState
import mtg.game.state.history.{GameEvent, LogEvent}

case class GameObjectActionResult(newGameObjectState: Option[GameObjectState], childActions: Seq[GameObjectAction], gameEvent: Option[GameEvent]) extends GameActionResult {
  override def logEvent: Option[LogEvent] = None
}

object GameObjectActionResult {
  implicit def updatedGameObjectState(gameObjectState: GameObjectState): GameObjectActionResult = optionalUpdatedGameObjectState(Some(gameObjectState))
  implicit def optionalUpdatedGameObjectState(gameObjectStateOption: Option[GameObjectState]): GameObjectActionResult = GameObjectActionResult(gameObjectStateOption, Nil, None)
  implicit def childAction(action: GameObjectAction): GameObjectActionResult = childActions(Seq(action))
  implicit def optionalChildAction(actionOption: Option[GameObjectAction]): GameObjectActionResult = childActions(actionOption.toSeq)
  implicit def childActions(actions: Seq[GameObjectAction]): GameObjectActionResult = GameObjectActionResult(None, actions, None)
  implicit def childActionAndEvent(tuple: (GameObjectAction, GameEvent)) = GameObjectActionResult(None, Seq(tuple._1), Some(tuple._2))
  implicit def childActionsAndEvent(tuple: (Seq[GameObjectAction], GameEvent)) = GameObjectActionResult(None, tuple._1, Some(tuple._2))
  implicit def nothing(unit: Unit): GameObjectActionResult = GameObjectActionResult(None, Nil, None)
}
