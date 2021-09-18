package mtg.effects.identifiers

import mtg.effects.StackObjectResolutionContext
import mtg.effects.filters.Filter
import mtg.game.ObjectOrPlayer
import mtg.game.state.GameState
import mtg.text.{GrammaticalNumber, NounPhrase}

case class FilterIdentifier[T <: ObjectOrPlayer](filter: Filter[T]) extends MultipleIdentifier[T] {
  override def getAll(gameState: GameState, resolutionContext: StackObjectResolutionContext): (Seq[T], StackObjectResolutionContext) = {
    (filter.getAll(resolutionContext, gameState).toSeq, resolutionContext)
  }
  override def getNounPhrase(cardName: String): NounPhrase = {
    NounPhrase.Templated(filter.getNounPhraseTemplate(cardName), GrammaticalNumber.Plural)
  }
}
