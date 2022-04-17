package mtg.abilities.keyword

import mtg.abilities.{KeywordAbility, StaticAbility}
import mtg.continuousEffects.ContinuousEffect
import mtg.effects.EffectContext
import mtg.game.state.ObjectWithState

case object Reach extends KeywordAbility {
  override def getEffects(effectContext: EffectContext): Seq[ContinuousEffect] = Nil
}
