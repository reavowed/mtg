package mtg.instructions.nounPhrases

import mtg.effects.InstructionResolutionContext
import mtg.game.state.GameState
import mtg.instructions.grammar.GrammaticalNumber
import mtg.instructions.{Instruction, IntransitiveInstructionVerb}

trait SingleIdentifyingNounPhrase[+T] extends SetIdentifyingNounPhrase[T] {
  def identifySingle(gameState: GameState, resolutionContext: InstructionResolutionContext): (T, InstructionResolutionContext)
  override def identifyAll(gameState: GameState, resolutionContext: InstructionResolutionContext): (Seq[T], InstructionResolutionContext) = {
    identifySingle(gameState, resolutionContext).mapLeft(Seq(_))
  }
  override def number: GrammaticalNumber = GrammaticalNumber.Singular
  def apply(verb: IntransitiveInstructionVerb[T]): Instruction = {
    IntransitiveInstructionVerb.WithSubject(this, verb)
  }
}
