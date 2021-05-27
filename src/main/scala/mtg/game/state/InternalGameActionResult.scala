package mtg.game.state

import mtg.game.state.history.LogEvent

case class InternalGameActionResult(childActions: Seq[GameAction], logEvent: Option[LogEvent])
object InternalGameActionResult {
  implicit def onlySingleChild(childAction: GameAction): InternalGameActionResult = InternalGameActionResult(Seq(childAction), None)
  implicit def onlyChildren(childActions: Seq[GameAction]): InternalGameActionResult = InternalGameActionResult(childActions, None)
  implicit def onlyLogEvent(logEvent: LogEvent): InternalGameActionResult = InternalGameActionResult(Nil, Some(logEvent))
  implicit def nothing(unit: Unit): InternalGameActionResult = InternalGameActionResult(Nil, None)
}
