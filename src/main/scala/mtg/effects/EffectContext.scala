package mtg.effects

import mtg.abilities.TriggeredAbility
import mtg.game.PlayerId
import mtg.game.state.{GameState, StackObjectWithState}

class EffectContext(val controllingPlayer: PlayerId, val sourceName: String)

object EffectContext {
  def apply(triggeredAbility: TriggeredAbility, gameState: GameState): EffectContext = {
    new EffectContext(
      triggeredAbility.ownerId,
      gameState.gameObjectState.getCurrentOrLastKnownState(triggeredAbility.sourceId).gameObject.underlyingObject.getSourceName(gameState))
  }
  def apply(stackObjectWithState: StackObjectWithState, gameState: GameState): EffectContext = {
    new EffectContext(
      stackObjectWithState.controller,
      stackObjectWithState.gameObject.underlyingObject.getSourceName(gameState))
  }
}
