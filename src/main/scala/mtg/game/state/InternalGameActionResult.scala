package mtg.game.state

import mtg.game.state.history.{GameEvent, LogEvent}

case class InternalGameActionResult(childActions: Seq[GameAction], gameEvent: Option[GameEvent], logEvent: Option[LogEvent])
object InternalGameActionResult {
  implicit def singleChild(childAction: GameAction): InternalGameActionResult = children(Seq(childAction))
  implicit def children(childActions: Seq[GameAction]): InternalGameActionResult = InternalGameActionResult(childActions, None, None)
  implicit def onlyLogEvent(logEvent: LogEvent): InternalGameActionResult = InternalGameActionResult(Nil, None, Some(logEvent))
  implicit def childrenAndGameEvent(tuple: (Seq[GameAction], GameEvent)): InternalGameActionResult = InternalGameActionResult(tuple._1, Some(tuple._2), None)
  implicit def childAndLogEvent(tuple: (GameAction, LogEvent)): InternalGameActionResult = InternalGameActionResult(Seq(tuple._1), None, Some(tuple._2))
  implicit def childrenAndLogEvent(tuple: (Seq[GameAction], LogEvent)): InternalGameActionResult = InternalGameActionResult(tuple._1, None, Some(tuple._2))
  implicit def all(tuple: (Seq[GameAction], GameEvent, LogEvent)): InternalGameActionResult = InternalGameActionResult(tuple._1, Some(tuple._2), Some(tuple._3))
  implicit def nothing(unit: Unit): InternalGameActionResult = InternalGameActionResult(Nil, None, None)
}
