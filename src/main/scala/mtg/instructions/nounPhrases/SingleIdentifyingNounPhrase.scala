package mtg.instructions.nounPhrases

import mtg.effects.StackObjectResolutionContext
import mtg.game.state.GameState
import mtg.instructions.{Instruction, IntransitiveInstructionVerb, VerbNumber}

trait SingleIdentifyingNounPhrase[+T] extends SetIdentifyingNounPhrase[T] {
  def identifySingle(gameState: GameState, resolutionContext: StackObjectResolutionContext): (T, StackObjectResolutionContext)
  override def identifyAll(gameState: GameState, resolutionContext: StackObjectResolutionContext): (Seq[T], StackObjectResolutionContext) = {
    identifySingle(gameState, resolutionContext).mapLeft(Seq(_))
  }
  override def number: VerbNumber = VerbNumber.Singular
  def apply(verb: IntransitiveInstructionVerb[T]): Instruction = {
    IntransitiveInstructionVerb.WithSubject(this, verb)
  }
}
