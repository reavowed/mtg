package mtg.effects

import mtg.abilities.TriggeredAbility
import mtg.core.{ObjectId, PlayerId}
import mtg.game.state.{GameState, ObjectWithState, StackObjectWithState}

class EffectContext(val cardNameObjectId: ObjectId, val thisObjectId: ObjectId, val youPlayerId: PlayerId)

object EffectContext {
  def apply(triggeredAbility: TriggeredAbility): EffectContext = {
    new EffectContext(triggeredAbility.sourceId, triggeredAbility.sourceId, triggeredAbility.controllerId)
  }
  def apply(objectWithState: ObjectWithState): EffectContext = {
    new EffectContext(objectWithState.gameObject.objectId, objectWithState.gameObject.objectId, objectWithState.controllerOrOwner)
  }
}
