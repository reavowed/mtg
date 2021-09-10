package mtg.effects.condition

import mtg.game.state.{AutomaticGameAction, GameState}

sealed trait Condition

trait EventCondition extends Condition {
  def matchesEvent(eventToMatch: AutomaticGameAction, gameState: GameState): Boolean
}

case class SingleEventCondition(event: AutomaticGameAction) extends EventCondition {
  override def matchesEvent(eventToMatch: AutomaticGameAction, gameState: GameState): Boolean = eventToMatch == event
}
