package mtg.game.state

import mtg.game.objects.GameObjectState
import mtg.game.state.history.{GameEvent, LogEvent}

trait GameActionResult {
  def newGameObjectState: Option[GameObjectState]
  def childActions: Seq[GameAction]
  def gameEvent: Option[GameEvent]
  def logEvent: Option[LogEvent]
}
