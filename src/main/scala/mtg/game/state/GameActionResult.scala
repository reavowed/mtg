package mtg.game.state

import mtg.game.objects.GameObjectState
import mtg.game.state.GameActionResult.childActions
import mtg.game.state.history.LogEvent

case class GameActionResult(newGameObjectState: Option[GameObjectState], childActions: Seq[GameAction], logEvent: Option[LogEvent])

object GameActionResult {
  implicit def updatedGameObjectState(gameObjectState: GameObjectState): GameActionResult = optionalUpdatedGameObjectState(Some(gameObjectState))
  implicit def optionalUpdatedGameObjectState(gameObjectStateOption: Option[GameObjectState]): GameActionResult = GameActionResult(gameObjectStateOption, Nil, None)
  implicit def gameObjectStateAndLogEvent(tuple: (GameObjectState, LogEvent)): GameActionResult = GameActionResult(Some(tuple._1), Nil, Some(tuple._2))
  implicit def childAction(childAction: GameAction): GameActionResult = GameActionResult(None, Seq(childAction), None)
  implicit def optionalChildAction(actionOption: Option[InternalGameAction]): GameActionResult = childActions(actionOption.toSeq)
  implicit def childActions(childActions: Seq[GameAction]): GameActionResult = GameActionResult(None, childActions, None)
  implicit def logEvent(logEvent: LogEvent): GameActionResult = GameActionResult(None, Nil, Some(logEvent))
  implicit def childAndLogEvent(tuple: (GameAction, LogEvent)): GameActionResult = GameActionResult(None, Seq(tuple._1), Some(tuple._2))
  implicit def childrenAndLogEvent(tuple: (Seq[GameAction], LogEvent)): GameActionResult = GameActionResult(None, tuple._1, Some(tuple._2))
  implicit def nothing(unit: Unit): GameActionResult = GameActionResult(None, Nil, None)
}
