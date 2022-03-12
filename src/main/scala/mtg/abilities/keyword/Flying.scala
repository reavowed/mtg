package mtg.abilities.keyword

import mtg.abilities.{KeywordAbility, StaticAbility}
import mtg.continuousEffects.{BlockerRestrictionEffect, ContinuousEffect}
import mtg.core.ObjectId
import mtg.game.state.ObjectWithState

case object Flying extends StaticAbility with KeywordAbility {
  override def getEffects(objectWithAbility: ObjectWithState): Seq[ContinuousEffect] = {
    Seq(FlyingEffect(objectWithAbility.gameObject.objectId))
  }
}

case class FlyingEffect(affectedObject: ObjectId) extends BlockerRestrictionEffect {
  override def preventsBlock(attackerState: ObjectWithState, blockerState: ObjectWithState): Boolean = {
    attackerState.gameObject.objectId == affectedObject && !(blockerState.characteristics.abilities.contains(Flying) || blockerState.characteristics.abilities.contains(Reach))
  }
}
