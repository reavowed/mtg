package mtg.effects.filters.combination

import mtg.effects.EffectContext
import mtg.effects.filters.Filter
import mtg.game.ObjectOrPlayer
import mtg.game.state.GameState

case class DisjunctiveFilter[T <: ObjectOrPlayer](innerFilters: Filter[T]*) extends Filter[T] {
  override def isValid(t: T, effectContext: EffectContext, gameState: GameState): Boolean = {
    innerFilters.exists(_.isValid(t, effectContext, gameState))
  }

  override def getText(cardName: String): String = innerFilters.map(_.getText(cardName)).mkString(" or ")
}
