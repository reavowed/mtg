package mtg.game.state.history

import mtg.game.turns.turnEvents.BeginTurnEvent

case class GameHistory(gameEvents: List[GameEvent], logEvents: Seq[TimestampedLogEvent]) {
  def addGameEvent(event: GameEvent): GameHistory = copy(gameEvents = event :: gameEvents)
  def addLogEvent(event: LogEvent): GameHistory = {
    copy(logEvents = logEvents :+ TimestampedLogEvent(event))
  }
  def gameEventsThisTurn: Iterable[GameEvent] = gameEvents.view.since[BeginTurnEvent]
}
object GameHistory {
  val empty = GameHistory(Nil, Nil)
}


