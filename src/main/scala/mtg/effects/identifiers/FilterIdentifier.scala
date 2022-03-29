package mtg.effects.identifiers

import mtg.core.ObjectOrPlayerId
import mtg.effects.StackObjectResolutionContext
import mtg.effects.filters.Filter
import mtg.game.state.GameState
import mtg.instructions.nouns.SetIdentifyingNounPhrase
import mtg.text.{VerbNumber, VerbPerson}

case class FilterIdentifier[T <: ObjectOrPlayerId](filter: Filter[T]) extends SetIdentifyingNounPhrase[T] {
  override def getText(cardName: String): String = filter.getNounPhraseTemplate(cardName).plural
  override def person: VerbPerson = VerbPerson.Third
  override def number: VerbNumber = VerbNumber.Plural
  override def identifyAll(gameState: GameState, resolutionContext: StackObjectResolutionContext): (Seq[T], StackObjectResolutionContext) = {
    (filter.getAll(gameState, resolutionContext).toSeq, resolutionContext)
  }
}
