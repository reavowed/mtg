package mtg.instructions.nounPhrases

import mtg.effects.{EffectContext, StackObjectResolutionContext}
import mtg.game.state.GameState
import mtg.instructions.{StateDescriptionVerb, SuffixDescriptor}

trait StaticSingleIdentifyingNounPhrase[T] extends SingleIdentifyingNounPhrase[T] {
  def identify(gameState: GameState, effectContext: EffectContext): T
  override def identifySingle(gameState: GameState, resolutionContext: StackObjectResolutionContext): (T, StackObjectResolutionContext) = {
    (identify(gameState, resolutionContext), resolutionContext)
  }

  def apply(verb: StateDescriptionVerb[T]): SuffixDescriptor = StateDescriptionVerb.WithSubject(this, verb)
}
