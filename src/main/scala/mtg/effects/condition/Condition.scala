package mtg.effects.condition

import mtg.game.state.GameEvent

sealed trait Condition

trait EventCondition extends Condition {
  def matchesEvent(eventToMatch: GameEvent): Boolean
}

case class SingleEventCondition(event: GameEvent) extends EventCondition {
  override def matchesEvent(eventToMatch: GameEvent): Boolean = eventToMatch == event
}
