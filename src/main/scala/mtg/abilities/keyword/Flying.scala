package mtg.abilities.keyword

import mtg.abilities.{KeywordAbility, StaticAbility}
import mtg.effects.ContinuousEffect
import mtg.effects.continuous.BlockerRestriction
import mtg.game.ObjectId
import mtg.game.state.ObjectWithState

case object Flying extends StaticAbility with KeywordAbility {
  override def getEffects(objectWithAbility: ObjectWithState): Seq[ContinuousEffect] = {
    Seq(FlyingRestriction(objectWithAbility.gameObject.objectId))
  }
}

case class FlyingRestriction(affectedObject: ObjectId) extends BlockerRestriction with ContinuousEffect.ForSingleObject {
  override def preventsBlock(attackerState: ObjectWithState, blockerState: ObjectWithState): Boolean = {
    attackerState.gameObject.objectId == affectedObject && !(blockerState.characteristics.abilities.contains(Flying) || blockerState.characteristics.abilities.contains(Reach))
  }
}
