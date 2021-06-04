package mtg.effects.filters.combination

import mtg.effects.filters.{Filter, PartialFilter}
import mtg.game.ObjectOrPlayer
import mtg.game.state.GameState

class SuffixFilter[T <: ObjectOrPlayer](mainFilter: Filter[T], suffixFilters: Seq[PartialFilter[T]]) extends Filter[T] {
  override def isValid(t: T, gameState: GameState): Boolean = suffixFilters.forall(_.matches(t, gameState)) && mainFilter.isValid(t, gameState)
  override def getText(cardName: String): String = (mainFilter +: suffixFilters).map(_.getText(cardName)).mkString(" ")
}
