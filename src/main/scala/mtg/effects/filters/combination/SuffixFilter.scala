package mtg.effects.filters.combination

import mtg.effects.EffectContext
import mtg.effects.filters.{Filter, PartialFilter}
import mtg.game.ObjectOrPlayer
import mtg.game.state.GameState

case class SuffixFilter[T <: ObjectOrPlayer](mainFilter: Filter[T], suffixFilters: Seq[PartialFilter[T]]) extends Filter[T] {
  override def isValid(t: T, effectContext: EffectContext, gameState: GameState): Boolean = {
    suffixFilters.forall(_.matches(t, effectContext, gameState)) && mainFilter.isValid(t, effectContext, gameState)
  }
  override def getText(cardName: String): String = (mainFilter +: suffixFilters).map(_.getText(cardName)).mkString(" ")
}
