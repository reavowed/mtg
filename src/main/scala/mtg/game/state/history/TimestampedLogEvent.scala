package mtg.game.state.history

import java.time.Instant

case class TimestampedLogEvent(logEvent: LogEvent, timestamp: Instant)
object TimestampedLogEvent {
  def apply(logEvent: LogEvent): TimestampedLogEvent = TimestampedLogEvent(logEvent, Instant.now())
}
