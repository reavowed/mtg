package mtg.instructions.nounPhrases

import mtg.instructions.grammar.GrammaticalNumber
import mtg.instructions.{Instruction, InstructionAction, IntransitiveInstructionVerb}

trait SingleIdentifyingNounPhrase[+T] extends SetIdentifyingNounPhrase[T] {
  def identifySingle: InstructionAction.WithResult[T]
  override def identifyAll: InstructionAction.WithResult[Seq[T]] = {
    identifySingle.map(Seq(_))
  }
  override def number: GrammaticalNumber = GrammaticalNumber.Singular
  def apply(verb: IntransitiveInstructionVerb[T]): Instruction = {
    IntransitiveInstructionVerb.WithSubject(this, verb)
  }
}
