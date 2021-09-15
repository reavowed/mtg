package mtg.effects.condition

import mtg.game.state.{GameState, InternalGameAction}

sealed trait Condition

trait EventCondition extends Condition {
  def matchesEvent(eventToMatch: InternalGameAction, gameState: GameState): Boolean
}

case class SingleEventCondition(event: InternalGameAction) extends EventCondition {
  override def matchesEvent(eventToMatch: InternalGameAction, gameState: GameState): Boolean = eventToMatch == event
}
