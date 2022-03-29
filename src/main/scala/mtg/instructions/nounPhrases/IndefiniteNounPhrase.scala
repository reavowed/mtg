package mtg.instructions.nounPhrases

import mtg.instructions.Descriptor
import mtg.text.{VerbNumber, VerbPerson}

trait IndefiniteNounPhrase[T] extends Descriptor[T] {
  def person: VerbPerson

  def number: VerbNumber
}
