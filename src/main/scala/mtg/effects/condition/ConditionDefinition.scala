package mtg.effects.condition

import mtg.effects.EffectContext
import mtg.game.state.GameState

trait ConditionDefinition {
  def getText(cardName: String): String
  def getCondition(gameState: GameState, effectContext: EffectContext): Condition
}
