package mtg.effects.condition

import mtg.game.state.{GameState, GameUpdate}

sealed trait Condition

trait EventCondition extends Condition {
  def matchesEvent(eventToMatch: GameUpdate, gameState: GameState): Boolean
}

case class SingleEventCondition(event: GameUpdate) extends EventCondition {
  override def matchesEvent(eventToMatch: GameUpdate, gameState: GameState): Boolean = eventToMatch == event
}
