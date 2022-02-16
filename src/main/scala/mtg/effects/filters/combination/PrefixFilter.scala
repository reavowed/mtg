package mtg.effects.filters.combination

import mtg.core.ObjectOrPlayerId
import mtg.effects.EffectContext
import mtg.effects.filters.{Filter, PartialFilter}
import mtg.game.state.GameState
import mtg.text.NounPhraseTemplate

class PrefixFilter[T <: ObjectOrPlayerId](prefixFilters: Seq[PartialFilter[T]], mainFilter: Filter[T]) extends Filter[T] {
  override def matches(t: T, effectContext: EffectContext, gameState: GameState): Boolean = {
    prefixFilters.forall(_.matches(t, effectContext, gameState)) && mainFilter.matches(t, effectContext, gameState)
  }

  override def getNounPhraseTemplate(cardName: String): NounPhraseTemplate = {
    mainFilter.getNounPhraseTemplate(cardName)
      .withPrefix(prefixFilters.map(_.getText(cardName)).mkString(" "))
  }

  override def getAll(effectContext: EffectContext, gameState: GameState): Set[T] = mainFilter.getAll(effectContext, gameState)
    .filter(o => prefixFilters.forall(f => f.matches(o, effectContext, gameState)))
}
