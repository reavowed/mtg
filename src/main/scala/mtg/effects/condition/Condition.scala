package mtg.effects.condition

import mtg.game.state.{GameState, GameUpdate}

sealed trait Condition {
  def matchesEvent(eventToMatch: GameUpdate, gameState: GameState): Boolean
}

case class SingleEventCondition(event: GameUpdate) extends Condition {
  override def matchesEvent(eventToMatch: GameUpdate, gameState: GameState): Boolean = eventToMatch == event
}
