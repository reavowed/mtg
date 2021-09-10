package mtg.game.state

import mtg.game.state.history.{GameEvent, LogEvent}

case class GameActionResult(childActions: Seq[GameAction], gameEvent: Option[GameEvent], logEvent: Option[LogEvent])
object GameActionResult {
  implicit def singleChild(childAction: GameAction): GameActionResult = children(Seq(childAction))
  implicit def children(childActions: Seq[GameAction]): GameActionResult = GameActionResult(childActions, None, None)
  implicit def onlyLogEvent(logEvent: LogEvent): GameActionResult = GameActionResult(Nil, None, Some(logEvent))
  implicit def childrenAndGameEvent(tuple: (Seq[GameAction], GameEvent)): GameActionResult = GameActionResult(tuple._1, Some(tuple._2), None)
  implicit def childAndLogEvent(tuple: (GameAction, LogEvent)): GameActionResult = GameActionResult(Seq(tuple._1), None, Some(tuple._2))
  implicit def childrenAndLogEvent(tuple: (Seq[GameAction], LogEvent)): GameActionResult = GameActionResult(tuple._1, None, Some(tuple._2))
  implicit def nothing(unit: Unit): GameActionResult = GameActionResult(Nil, None, None)
}
