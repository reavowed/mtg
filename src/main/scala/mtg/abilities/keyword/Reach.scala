package mtg.abilities.keyword

import mtg.abilities.{KeywordAbility, StaticAbility}
import mtg.effects.ContinuousObjectEffect
import mtg.effects.continuous.BlockerRestriction
import mtg.game.ObjectId
import mtg.game.state.ObjectWithState

case object Reach extends StaticAbility with KeywordAbility {
  override def getEffects(objectWithAbility: ObjectWithState): Seq[ContinuousObjectEffect] = Nil
}
