package mtg.effects

import mtg.abilities.TriggeredAbility
import mtg.definitions.{ObjectId, PlayerId}
import mtg.game.state.ObjectWithState

class EffectContext(val cardNameObjectId: ObjectId, val thisObjectId: ObjectId, val youPlayerId: PlayerId)

object EffectContext {
  def apply(triggeredAbility: TriggeredAbility): EffectContext = {
    new EffectContext(triggeredAbility.sourceId, triggeredAbility.sourceId, triggeredAbility.controllerId)
  }
  def apply(objectWithState: ObjectWithState): EffectContext = {
    new EffectContext(objectWithState.gameObject.objectId, objectWithState.gameObject.objectId, objectWithState.controllerOrOwner)
  }
}
