package mtg.abilities.keyword

import mtg.abilities.{KeywordAbility, StaticAbility}
import mtg.continuousEffects.{BlockerRestrictionEffect, ContinuousEffect}
import mtg.core.ObjectId
import mtg.effects.EffectContext
import mtg.game.state.ObjectWithState

case object Flying extends KeywordAbility {
  override def getEffects(effectContext: EffectContext): Seq[ContinuousEffect] = {
    Seq(FlyingEffect(effectContext.thisObjectId))
  }
}

case class FlyingEffect(affectedObject: ObjectId) extends BlockerRestrictionEffect {
  override def preventsBlock(attackerState: ObjectWithState, blockerState: ObjectWithState): Boolean = {
    attackerState.gameObject.objectId == affectedObject && !(blockerState.characteristics.abilities.contains(Flying) || blockerState.characteristics.abilities.contains(Reach))
  }
}
