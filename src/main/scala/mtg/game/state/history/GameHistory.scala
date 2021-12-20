package mtg.game.state.history

case class GameHistory(historyEvents: List[HistoryEvent], logEvents: Seq[TimestampedLogEvent]) {
  def addGameEvent(event: HistoryEvent): GameHistory = copy(historyEvents = event :: historyEvents)
  def addLogEvent(event: LogEvent): GameHistory = {
    copy(logEvents = logEvents :+ TimestampedLogEvent(event))
  }
  def addLogEvent(event: Option[LogEvent]): GameHistory = {
    event.map(addLogEvent).getOrElse(this)
  }
  def gameEventsThisTurn: Iterable[HistoryEvent] = historyEvents.headOption.flatMap(_.stateBefore.currentTurn) match {
    case None => historyEvents
    case Some(turn) => historyEvents.takeWhile(_.stateBefore.currentTurn.contains(turn))
  }
}
object GameHistory {
  val empty = GameHistory(Nil, Nil)
}


