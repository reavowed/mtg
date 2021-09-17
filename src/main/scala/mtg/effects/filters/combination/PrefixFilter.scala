package mtg.effects.filters.combination

import mtg.effects.EffectContext
import mtg.effects.filters.{Filter, PartialFilter}
import mtg.game.{ObjectId, ObjectOrPlayer}
import mtg.game.state.GameState

class PrefixFilter[T <: ObjectOrPlayer](prefixFilters: Seq[PartialFilter[T]], mainFilter: Filter[T]) extends Filter[T] {
  override def matches(t: T, effectContext: EffectContext, gameState: GameState): Boolean = {
    prefixFilters.forall(_.matches(t, effectContext, gameState)) && mainFilter.matches(t, effectContext, gameState)
  }
  override def getText(cardName: String): String = (prefixFilters.map(_.getText(cardName)) :+ mainFilter.getText(cardName)).mkString(" ")

  override def getAll(effectContext: EffectContext, gameState: GameState): Set[T] = mainFilter.getAll(effectContext, gameState)
    .filter(o => prefixFilters.forall(f => f.matches(o, effectContext, gameState)))
}
