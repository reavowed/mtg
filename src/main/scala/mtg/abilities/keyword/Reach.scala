package mtg.abilities.keyword

import mtg.abilities.{KeywordAbility, StaticAbility}
import mtg.continuousEffects.ContinuousEffect
import mtg.game.state.ObjectWithState

case object Reach extends KeywordAbility {
  override def getEffects(objectWithAbility: ObjectWithState): Seq[ContinuousEffect] = Nil
}
