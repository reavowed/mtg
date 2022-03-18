package mtg.effects.filters.combination

import mtg.core.ObjectOrPlayerId
import mtg.effects.EffectContext
import mtg.effects.filters.Filter
import mtg.game.state.GameState
import mtg.text.NounPhraseTemplate

case class DisjunctiveFilter[T <: ObjectOrPlayerId](innerFilters: Filter[T]*) extends Filter[T] {
  override def matches(t: T, gameState: GameState, effectContext: EffectContext): Boolean = {
    innerFilters.exists(_.matches(t, gameState, effectContext))
  }

  override def getNounPhraseTemplate(cardName: String): NounPhraseTemplate = {
    NounPhraseTemplate.Compound(innerFilters.map(_.getNounPhraseTemplate(cardName)), "or")
  }

  override def getAll(gameState: GameState, effectContext: EffectContext): Set[T] = innerFilters.map(_.getAll(gameState, effectContext)).reduce(_ ++ _)
}
