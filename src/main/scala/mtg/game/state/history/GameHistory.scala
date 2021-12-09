package mtg.game.state.history

import mtg.game.turns.turnEvents.BeginTurnEvent

case class GameHistory(historyEvents: List[HistoryEvent], logEvents: Seq[TimestampedLogEvent]) {
  def addGameEvent(event: HistoryEvent): GameHistory = copy(historyEvents = event :: historyEvents)
  def addLogEvent(event: LogEvent): GameHistory = {
    copy(logEvents = logEvents :+ TimestampedLogEvent(event))
  }
  def addLogEvent(event: Option[LogEvent]): GameHistory = {
    event.map(addLogEvent).getOrElse(this)
  }
  def gameEventsThisTurn: Iterable[HistoryEvent] = historyEvents.view.since[BeginTurnEvent]
}
object GameHistory {
  val empty = GameHistory(Nil, Nil)
}


