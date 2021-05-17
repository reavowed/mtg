package mtg.web.visibleState

import mtg.game.state.history.{LogEvent, TimestampedLogEvent}

case class LogEventWrapper(`type`: String, timestamp: Long, details: LogEvent)
object LogEventWrapper {
  def apply(event: TimestampedLogEvent): LogEventWrapper = LogEventWrapper(
    event.logEvent.getClass.getSimpleName,
    event.timestamp.getEpochSecond,
    event.logEvent)
}
