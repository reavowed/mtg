package mtg.game.state

import mtg.game.objects.GameObjectState
import mtg.game.state.GameActionResult.nextUpdates
import mtg.game.state.history.LogEvent

case class GameActionResult(newGameObjectState: Option[GameObjectState], nextUpdates: Seq[GameUpdate], logEvent: Option[LogEvent])

object GameActionResult {
  implicit def updatedGameObjectState(gameObjectState: GameObjectState): GameActionResult = optionalUpdatedGameObjectState(Some(gameObjectState))
  implicit def optionalUpdatedGameObjectState(gameObjectStateOption: Option[GameObjectState]): GameActionResult = GameActionResult(gameObjectStateOption, Nil, None)
  implicit def gameObjectStateAndLogEvent(tuple: (GameObjectState, LogEvent)): GameActionResult = GameActionResult(Some(tuple._1), Nil, Some(tuple._2))
  implicit def nextUpdate(childAction: GameUpdate): GameActionResult = GameActionResult(None, Seq(childAction), None)
  implicit def optionalNextUpdate(actionOption: Option[InternalGameAction]): GameActionResult = nextUpdates(actionOption.toSeq)
  implicit def nextUpdates(childActions: Seq[GameUpdate]): GameActionResult = GameActionResult(None, childActions, None)
  implicit def logEvent(logEvent: LogEvent): GameActionResult = GameActionResult(None, Nil, Some(logEvent))
  implicit def nextUpdateAndLogEvent(tuple: (GameUpdate, LogEvent)): GameActionResult = GameActionResult(None, Seq(tuple._1), Some(tuple._2))
  implicit def nextUpdatesAndLogEvent(tuple: (Seq[GameUpdate], LogEvent)): GameActionResult = GameActionResult(None, tuple._1, Some(tuple._2))
  implicit def nothing(unit: Unit): GameActionResult = GameActionResult(None, Nil, None)
}
