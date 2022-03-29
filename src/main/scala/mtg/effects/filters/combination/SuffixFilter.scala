package mtg.effects.filters.combination

import mtg.core.ObjectOrPlayerId
import mtg.effects.EffectContext
import mtg.effects.filters.{Filter, PartialFilter}
import mtg.game.state.GameState
import mtg.text.NounPhraseTemplate

case class SuffixFilter[T <: ObjectOrPlayerId](mainFilter: Filter[T], suffixFilters: Seq[PartialFilter[T]]) extends Filter[T] {
  override def getSingular(cardName: String): String = (mainFilter.getSingular(cardName) +: suffixFilters.map(_.getText(cardName))).mkString(" ")
  override def getPlural(cardName: String): String = (mainFilter.getPlural(cardName) +: suffixFilters.map(_.getText(cardName))).mkString(" ")
  override def getAll(gameState: GameState, effectContext: EffectContext): Seq[T] = {
    mainFilter.getAll(gameState, effectContext).filter(t => suffixFilters.forall(_.matches(t, gameState, effectContext)))
  }
}
