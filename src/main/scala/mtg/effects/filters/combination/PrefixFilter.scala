package mtg.effects.filters.combination

import mtg.effects.EffectContext
import mtg.effects.filters.{Filter, PartialFilter}
import mtg.game.ObjectOrPlayer
import mtg.game.state.GameState

class PrefixFilter[T <: ObjectOrPlayer](prefixFilters: Seq[PartialFilter[T]], mainFilter: Filter[T]) extends Filter[T] {
  override def isValid(t: T, effectContext: EffectContext, gameState: GameState): Boolean = {
    prefixFilters.forall(_.matches(t, effectContext, gameState)) && mainFilter.isValid(t, effectContext, gameState)
  }
  override def getText(cardName: String): String = (prefixFilters :+ mainFilter).map(_.getText(cardName)).mkString(" ")
}
