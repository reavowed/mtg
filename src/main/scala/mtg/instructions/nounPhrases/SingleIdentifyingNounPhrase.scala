package mtg.instructions.nounPhrases

import mtg.effects.StackObjectResolutionContext
import mtg.game.state.GameState
import mtg.instructions.grammar.GrammaticalNumber
import mtg.instructions.{Instruction, IntransitiveInstructionVerb}

trait SingleIdentifyingNounPhrase[+T] extends SetIdentifyingNounPhrase[T] {
  def identifySingle(gameState: GameState, resolutionContext: StackObjectResolutionContext): (T, StackObjectResolutionContext)
  override def identifyAll(gameState: GameState, resolutionContext: StackObjectResolutionContext): (Seq[T], StackObjectResolutionContext) = {
    identifySingle(gameState, resolutionContext).mapLeft(Seq(_))
  }
  override def number: GrammaticalNumber = GrammaticalNumber.Singular
  def apply(verb: IntransitiveInstructionVerb[T]): Instruction = {
    IntransitiveInstructionVerb.WithSubject(this, verb)
  }
}
