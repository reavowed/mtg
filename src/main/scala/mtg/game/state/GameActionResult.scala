package mtg.game.state

import mtg.game.objects.GameObjectState
import mtg.game.state.history.LogEvent

trait GameActionResult {
  def newGameObjectState: Option[GameObjectState]
  def childActions: Seq[GameAction]
  def logEvent: Option[LogEvent]
}
