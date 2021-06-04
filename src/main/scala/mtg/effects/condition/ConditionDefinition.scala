package mtg.effects.condition

import mtg.effects.StackObjectResolutionContext
import mtg.game.state.GameState

trait ConditionDefinition {
  def getText(cardName: String): String
  def getCondition(gameState: GameState, resolutionContext: StackObjectResolutionContext): Condition
}
