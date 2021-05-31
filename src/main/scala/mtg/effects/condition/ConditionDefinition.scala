package mtg.effects.condition

import mtg.effects.oneshot.OneShotEffectResolutionContext
import mtg.game.state.GameState

trait ConditionDefinition {
  def getText(cardName: String): String
  def getCondition(gameState: GameState, resolutionContext: OneShotEffectResolutionContext): Condition
}
