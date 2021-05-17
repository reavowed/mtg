package mtg.game.state

import mtg.game.PlayerIdentifier
import mtg.game.turns.Turn

import java.time.Instant
import scala.reflect.{ClassTag, classTag}

case class GameHistory(preGame: EventHistory, turns: Seq[TurnHistory]) {
  def startTurn(turn: Turn): GameHistory = {
    copy(turns = turns :+ TurnHistory(turn, EventHistory.empty))
  }

  private def updateCurrentTurn(f: EventHistory => EventHistory): GameHistory = {
    turns match {
      case previousTurns :+ currentTurn =>
        copy(turns = previousTurns :+ currentTurn.copy(eventHistory = f(currentTurn.eventHistory)))
      case Nil =>
        copy(preGame = f(preGame))
    }
  }
  def addGameEvent(event: GameEvent): GameHistory = updateCurrentTurn(_.addGameEvent(event))
  def addLogEvent(event: LogEvent): GameHistory = updateCurrentTurn(_.addLogEvent(event))

  def allLogEvents: Seq[TimestampedLogEvent] = (preGame +: turns.map(_.eventHistory)).flatMap(_.logEvents)
}
object GameHistory {
  val empty = GameHistory(EventHistory.empty, Nil)
}

case class TurnHistory(turn: Turn, eventHistory: EventHistory)

case class EventHistory(gameEvents: Seq[GameEvent], logEvents: Seq[TimestampedLogEvent]) {
  def addGameEvent(event: GameEvent): EventHistory = copy(gameEvents = gameEvents :+ event)
  def addLogEvent(event: LogEvent): EventHistory = copy(logEvents = logEvents :+ TimestampedLogEvent(event))
}
object EventHistory {
  val empty = EventHistory(Nil, Nil)
}

case class TimestampedLogEvent(logEvent: LogEvent, timestamp: Instant)
object TimestampedLogEvent {
  def apply(logEvent: LogEvent): TimestampedLogEvent = TimestampedLogEvent(logEvent, Instant.now())
}


sealed trait GameEvent
object GameEvent {
  case class Decision(chosenOption: ChoiceOption, playerIdentifier: PlayerIdentifier) extends GameEvent
  case class ResolvedEvent(event: GameObjectEvent) extends GameEvent

  implicit class GameEventSeqOps(seq: Seq[GameEvent]) {
    def sinceEvent[T <: GameObjectEvent : ClassTag]: Seq[GameEvent] = seq.takeRightUntil {
      case ResolvedEvent(e) if classTag[T].runtimeClass.isInstance(e) => true
      case _ => false
    }
  }
}
