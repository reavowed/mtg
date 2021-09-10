package mtg.effects.condition

import mtg.game.state.GameState
import mtg.game.state.history.GameEvent

sealed trait Condition

trait EventCondition extends Condition {
  def matchesEvent(eventToMatch: GameEvent, gameState: GameState): Boolean
}

case class SingleEventCondition(event: GameEvent) extends EventCondition {
  override def matchesEvent(eventToMatch: GameEvent, gameState: GameState): Boolean = eventToMatch == event
}
