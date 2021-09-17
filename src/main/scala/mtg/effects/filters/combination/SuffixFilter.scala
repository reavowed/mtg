package mtg.effects.filters.combination

import mtg.effects.EffectContext
import mtg.effects.filters.{Filter, PartialFilter}
import mtg.game.ObjectOrPlayer
import mtg.game.state.GameState

case class SuffixFilter[T <: ObjectOrPlayer](mainFilter: Filter[T], suffixFilters: Seq[PartialFilter[T]]) extends Filter[T] {
  override def matches(t: T, effectContext: EffectContext, gameState: GameState): Boolean = {
    suffixFilters.forall(_.matches(t, effectContext, gameState)) && mainFilter.matches(t, effectContext, gameState)
  }
  override def getText(cardName: String): String = (mainFilter.getText(cardName) +: suffixFilters.map(_.getText(cardName))).mkString(" ")

  override def getAll(effectContext: EffectContext, gameState: GameState): Set[T] = mainFilter.getAll(effectContext, gameState)
    .filter(o => suffixFilters.forall(f => f.matches(o, effectContext, gameState)))
}
