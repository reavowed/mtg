package mtg.effects.condition

import mtg.game.state.{GameEvent, GameState}

sealed trait Condition

trait EventCondition extends Condition {
  def matchesEvent(eventToMatch: GameEvent, gameState: GameState): Boolean
}

case class SingleEventCondition(event: GameEvent) extends EventCondition {
  override def matchesEvent(eventToMatch: GameEvent, gameState: GameState): Boolean = eventToMatch == event
}
