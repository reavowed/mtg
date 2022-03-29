package mtg.effects.filters.combination

import mtg.core.ObjectOrPlayerId
import mtg.effects.EffectContext
import mtg.effects.filters.{Filter, PartialFilter}
import mtg.game.state.GameState
import mtg.text.NounPhraseTemplate

class PrefixFilter[T <: ObjectOrPlayerId](prefixFilters: Seq[PartialFilter[T]], mainFilter: Filter[T]) extends Filter[T] {
  override def getSingular(cardName: String): String = (prefixFilters.map(_.getText(cardName)) :+ mainFilter.getSingular(cardName)).mkString(" ")
  override def getPlural(cardName: String): String = (prefixFilters.map(_.getText(cardName)) :+ mainFilter.getPlural(cardName)).mkString(" ")
  override def describes(t: T, gameState: GameState, effectContext: EffectContext): Boolean = {
    prefixFilters.forall(_.matches(t, gameState, effectContext)) && mainFilter.describes(t, gameState, effectContext)
  }
  override def getAll(gameState: GameState, effectContext: EffectContext): Set[T] = mainFilter.getAll(gameState, effectContext)
    .filter(o => prefixFilters.forall(f => f.matches(o, gameState, effectContext)))
}
