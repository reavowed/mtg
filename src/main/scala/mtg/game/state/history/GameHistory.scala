package mtg.game.state.history

import mtg.game.state.GameState

case class GameEventWithPreviousState(gameEvent: GameEvent, stateBefore: GameState)

case class GameHistory(gameEventsWithPreviousStates: List[GameEventWithPreviousState], logEvents: Seq[TimestampedLogEvent]) {
  def addGameEvent(event: GameEvent, state: GameState): GameHistory = {
    copy(gameEventsWithPreviousStates = GameEventWithPreviousState(event, state) :: gameEventsWithPreviousStates)
  }

  def addLogEvent(event: LogEvent): GameHistory = {
    copy(logEvents = logEvents :+ TimestampedLogEvent(event))
  }

  def recentEventsWhile(predicate: GameEventWithPreviousState => Boolean): Iterable[GameEventWithPreviousState] = new Iterable[GameEventWithPreviousState] {
    override def iterator: Iterator[GameEventWithPreviousState] = {
      gameEventsWithPreviousStates.iterator.takeWhile(predicate)
    }
  }
}
object GameHistory {
  val empty = GameHistory(Nil, Nil)
}


