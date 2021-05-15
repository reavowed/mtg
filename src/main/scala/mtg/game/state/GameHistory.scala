package mtg.game.state

import mtg.events.Event
import mtg.game.PlayerIdentifier

case class GameHistory(preGameEvents: Seq[GameEvent]) {
  def addEvents(events: Seq[GameEvent]): GameHistory = copy(preGameEvents = preGameEvents ++ events)
}

object GameHistory {
  val empty = GameHistory(Nil)
}

sealed trait GameEvent
object GameEvent {
  case class Decision[TOption <: Option](chosenOption: TOption, playerIdentifier: PlayerIdentifier) extends GameEvent
  case class ResolvedEvent(event: Event) extends GameEvent
}
