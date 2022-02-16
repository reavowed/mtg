package mtg.effects

import mtg.abilities.TriggeredAbility
import mtg.core.PlayerId
import mtg.game.state.{GameState, StackObjectWithState}

class EffectContext(val controllingPlayer: PlayerId, val sourceName: String)

object EffectContext {
  def apply(triggeredAbility: TriggeredAbility, gameState: GameState): EffectContext = {
    new EffectContext(
      triggeredAbility.ownerId,
      triggeredAbility.getSourceName(gameState).get)
  }
  def apply(stackObjectWithState: StackObjectWithState, gameState: GameState): EffectContext = {
    new EffectContext(
      stackObjectWithState.controller,
      stackObjectWithState.gameObject.underlyingObject.getSourceName(gameState))
  }
}
