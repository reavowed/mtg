package mtg.game.state

import mtg.game.objects.GameObjectState
import mtg.game.state.history.LogEvent

case class GameActionResult(newGameObjectState: Option[GameObjectState], nextActions: Seq[InternalGameAction], logEvent: Option[LogEvent])

object GameActionResult {
  implicit def updatedGameObjectState(gameObjectState: GameObjectState): GameActionResult = optionalUpdatedGameObjectState(Some(gameObjectState))
  implicit def optionalUpdatedGameObjectState(gameObjectStateOption: Option[GameObjectState]): GameActionResult = GameActionResult(gameObjectStateOption, Nil, None)
  implicit def gameObjectStateAndLogEvent(tuple: (GameObjectState, LogEvent)): GameActionResult = GameActionResult(Some(tuple._1), Nil, Some(tuple._2))
  implicit def nextAction(childAction: InternalGameAction): GameActionResult = GameActionResult(None, Seq(childAction), None)
  implicit def optionalNextAction(actionOption: Option[InternalGameAction]): GameActionResult = nextActions(actionOption.toSeq)
  implicit def nextActions(childActions: Seq[InternalGameAction]): GameActionResult = GameActionResult(None, childActions, None)
  implicit def logEvent(logEvent: LogEvent): GameActionResult = GameActionResult(None, Nil, Some(logEvent))
  implicit def nextActionAndLogEvent(tuple: (InternalGameAction, LogEvent)): GameActionResult = GameActionResult(None, Seq(tuple._1), Some(tuple._2))
  implicit def nextActionsAndLogEvent(tuple: (Seq[InternalGameAction], LogEvent)): GameActionResult = GameActionResult(None, tuple._1, Some(tuple._2))
  implicit def nothing(unit: Unit): GameActionResult = GameActionResult(None, Nil, None)
}
