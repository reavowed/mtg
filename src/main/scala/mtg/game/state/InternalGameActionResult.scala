package mtg.game.state

import mtg.game.objects.GameObjectState
import mtg.game.state.history.LogEvent

case class InternalGameActionResult(childActions: Seq[GameAction], logEvent: Option[LogEvent]) extends GameActionResult {
  override def newGameObjectState: Option[GameObjectState] = None
}
object InternalGameActionResult {
  implicit def onlySingleChild(childAction: GameAction): InternalGameActionResult = InternalGameActionResult(Seq(childAction), None)
  implicit def onlyChildren(childActions: Seq[GameAction]): InternalGameActionResult = InternalGameActionResult(childActions, None)
  implicit def onlyLogEvent(logEvent: LogEvent): InternalGameActionResult = InternalGameActionResult(Nil, Some(logEvent))
  implicit def childAndLogEvent(tuple: (GameAction, LogEvent)): InternalGameActionResult = InternalGameActionResult(Seq(tuple._1), Some(tuple._2))
  implicit def childrenAndLogEvent(tuple: (Seq[GameAction], LogEvent)): InternalGameActionResult = InternalGameActionResult(tuple._1, Some(tuple._2))
  implicit def nothing(unit: Unit): InternalGameActionResult = InternalGameActionResult(Nil, None)
}
