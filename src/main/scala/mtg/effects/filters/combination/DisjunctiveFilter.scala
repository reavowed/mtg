package mtg.effects.filters.combination

import mtg.effects.EffectContext
import mtg.effects.filters.Filter
import mtg.game.ObjectOrPlayer
import mtg.game.state.GameState
import mtg.text.NounPhraseTemplate

case class DisjunctiveFilter[T <: ObjectOrPlayer](innerFilters: Filter[T]*) extends Filter[T] {
  override def matches(t: T, effectContext: EffectContext, gameState: GameState): Boolean = {
    innerFilters.exists(_.matches(t, effectContext, gameState))
  }

  override def getNounPhraseTemplate(cardName: String): NounPhraseTemplate = {
    NounPhraseTemplate.Compound(innerFilters.map(_.getNounPhraseTemplate(cardName)), "or")
  }

  override def getAll(effectContext: EffectContext, gameState: GameState): Set[T] = innerFilters.map(_.getAll(effectContext, gameState)).reduce(_ ++ _)
}
