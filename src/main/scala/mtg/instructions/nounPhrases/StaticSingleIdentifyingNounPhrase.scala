package mtg.instructions.nounPhrases

import mtg.abilities.StaticAbilityParagraph
import mtg.effects.{EffectContext, InstructionResolutionContext}
import mtg.game.state.GameState
import mtg.instructions.{IntransitiveStaticAbilityVerb, StateDescriptionVerb, SuffixDescriptor}

trait StaticSingleIdentifyingNounPhrase[+T] extends SingleIdentifyingNounPhrase[T] {
  def identify(effectContext: EffectContext): T
  override def identifySingle(gameState: GameState, resolutionContext: InstructionResolutionContext): (T, InstructionResolutionContext) = {
    (identify(resolutionContext), resolutionContext)
  }

  def apply(verb: StateDescriptionVerb[T]): SuffixDescriptor = StateDescriptionVerb.WithSubject(this, verb)
  def apply(verb: IntransitiveStaticAbilityVerb[T]): StaticAbilityParagraph = {
    IntransitiveStaticAbilityVerb.WithSubject(this, verb)
  }
}
