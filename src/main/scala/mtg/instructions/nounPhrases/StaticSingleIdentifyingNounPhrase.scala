package mtg.instructions.nounPhrases

import mtg.abilities.StaticAbilityParagraph
import mtg.effects.EffectContext
import mtg.instructions.{InstructionAction, IntransitiveStaticAbilityVerb, StateDescriptionVerb, SuffixDescriptor}

trait StaticSingleIdentifyingNounPhrase[+T] extends SingleIdentifyingNounPhrase[T] {
  def identify(effectContext: EffectContext): T
  override def identifySingle: InstructionAction.WithResult[T] = InstructionAction.WithResult.withoutContextUpdate { (resolutionContext, _) =>
    identify(resolutionContext)
  }

  def apply(verb: StateDescriptionVerb[T]): SuffixDescriptor = StateDescriptionVerb.WithSubject(this, verb)
  def apply(verb: IntransitiveStaticAbilityVerb[T]): StaticAbilityParagraph = {
    IntransitiveStaticAbilityVerb.WithSubject(this, verb)
  }
}
