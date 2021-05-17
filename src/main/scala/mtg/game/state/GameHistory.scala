package mtg.game.state

import monocle.{Focus, Lens}
import mtg.game.PlayerIdentifier

import java.time.Instant
import scala.reflect.{ClassTag, classTag}

case class GameHistory(preGame: TurnHistory) {
  private def currentTurnLens: Lens[GameHistory, TurnHistory] = Focus[GameHistory](_.preGame)
  def addGameEvent(event: GameEvent): GameHistory = currentTurnLens.modify(_.addGameEvent(event))(this)
  def addLogEvent(event: LogEvent): GameHistory = currentTurnLens.modify(_.addLogEvent(event))(this)

  def allLogEvents: Seq[TimestampedLogEvent] = preGame.logEvents
}
object GameHistory {
  val empty = GameHistory(TurnHistory.empty)
}

case class TurnHistory(gameEvents: Seq[GameEvent], logEvents: Seq[TimestampedLogEvent]) {
  def addGameEvent(event: GameEvent): TurnHistory = copy(gameEvents = gameEvents :+ event)
  def addLogEvent(event: LogEvent): TurnHistory = copy(logEvents = logEvents :+ TimestampedLogEvent(event))
}
object TurnHistory {
  val empty = TurnHistory(Nil, Nil)
}

case class TimestampedLogEvent(logEvent: LogEvent, timestamp: Instant)
object TimestampedLogEvent {
  def apply(logEvent: LogEvent): TimestampedLogEvent = TimestampedLogEvent(logEvent, Instant.now())
}


sealed trait GameEvent
object GameEvent {
  case class Decision(chosenOption: GameOption, playerIdentifier: PlayerIdentifier) extends GameEvent
  case class ResolvedEvent(event: GameObjectEvent) extends GameEvent

  implicit class GameEventSeqOps(seq: Seq[GameEvent]) {
    def sinceEvent[T <: GameObjectEvent : ClassTag]: Seq[GameEvent] = seq.takeRightUntil {
      case ResolvedEvent(e) if classTag[T].runtimeClass.isInstance(e) => true
      case _ => false
    }
  }
}
