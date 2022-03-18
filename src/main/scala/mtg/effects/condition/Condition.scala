package mtg.effects.condition

import mtg.effects.EffectContext
import mtg.game.state.{GameState, GameUpdate}

trait Condition {
  def getText(cardName: String): String
  def matchesEvent(eventToMatch: GameUpdate, gameState: GameState, effectContext: EffectContext): Boolean
}
