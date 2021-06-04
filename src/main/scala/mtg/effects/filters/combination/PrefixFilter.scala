package mtg.effects.filters.combination

import mtg.effects.filters.{Filter, PartialFilter}
import mtg.game.ObjectOrPlayer
import mtg.game.state.GameState

class PrefixFilter[T <: ObjectOrPlayer](prefixFilters: Seq[PartialFilter[T]], mainFilter: Filter[T]) extends Filter[T] {
  override def isValid(t: T, gameState: GameState): Boolean = prefixFilters.forall(_.matches(t, gameState)) && mainFilter.isValid(t, gameState)
  override def getText(cardName: String): String = (prefixFilters :+ mainFilter).map(_.getText(cardName)).mkString(" ")
}
