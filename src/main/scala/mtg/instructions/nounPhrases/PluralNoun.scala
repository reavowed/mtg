package mtg.instructions.nounPhrases

import mtg.core.ObjectOrPlayerId
import mtg.effects.StackObjectResolutionContext
import mtg.game.state.GameState
import mtg.instructions.nouns.Noun
import mtg.text.{VerbNumber, VerbPerson}

import scala.reflect.ClassTag

case class PluralNoun[T <: ObjectOrPlayerId : ClassTag](noun: Noun[T]) extends SetIdentifyingNounPhrase[T] {
  override def getText(cardName: String): String = noun.getPlural(cardName)
  override def person: VerbPerson = VerbPerson.Third
  override def number: VerbNumber = VerbNumber.Plural
  override def identifyAll(gameState: GameState, resolutionContext: StackObjectResolutionContext): (Seq[T], StackObjectResolutionContext) = {
    (noun.getAll(gameState, resolutionContext), resolutionContext)
  }
}
