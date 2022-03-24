package mtg.effects

import mtg.abilities.TriggeredAbility
import mtg.core.{ObjectId, PlayerId}
import mtg.game.state.{GameState, StackObjectWithState}

class EffectContext(val sourceId: ObjectId, val controllingPlayer: PlayerId)

object EffectContext {
  def apply(triggeredAbility: TriggeredAbility): EffectContext = {
    new EffectContext(triggeredAbility.sourceId, triggeredAbility.ownerId)
  }
  def apply(stackObjectWithState: StackObjectWithState): EffectContext = {
    new EffectContext(stackObjectWithState.gameObject.objectId, stackObjectWithState.controller)
  }
}
