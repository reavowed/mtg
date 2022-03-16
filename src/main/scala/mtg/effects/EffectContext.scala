package mtg.effects

import mtg.abilities.TriggeredAbility
import mtg.core.PlayerId
import mtg.game.state.{GameState, StackObjectWithState}

class EffectContext(val controllingPlayer: PlayerId)

object EffectContext {
  def apply(triggeredAbility: TriggeredAbility): EffectContext = {
    new EffectContext(triggeredAbility.ownerId)
  }
  def apply(stackObjectWithState: StackObjectWithState): EffectContext = {
    new EffectContext(stackObjectWithState.controller)
  }
}
