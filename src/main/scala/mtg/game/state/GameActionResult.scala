package mtg.game.state

import mtg.game.state.history.LogEvent

case class GameActionResult(childActions: Seq[GameAction], logEvent: Option[LogEvent])
object GameActionResult {
  implicit def onlySingleChild(childAction: GameAction): GameActionResult = GameActionResult(Seq(childAction), None)
  implicit def onlyChildren(childActions: Seq[GameAction]): GameActionResult = GameActionResult(childActions, None)
  implicit def onlyLogEvent(logEvent: LogEvent): GameActionResult = GameActionResult(Nil, Some(logEvent))
  implicit def childAndLogEvent(tuple: (GameAction, LogEvent)): GameActionResult = GameActionResult(Seq(tuple._1), Some(tuple._2))
  implicit def childrenAndLogEvent(tuple: (Seq[GameAction], LogEvent)): GameActionResult = GameActionResult(tuple._1, Some(tuple._2))
  implicit def nothing(unit: Unit): GameActionResult = GameActionResult(Nil, None)
}
