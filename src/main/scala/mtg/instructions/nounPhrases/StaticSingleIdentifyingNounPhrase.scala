package mtg.instructions.nounPhrases

import mtg.effects.{EffectContext, StackObjectResolutionContext}
import mtg.game.state.GameState

trait StaticSingleIdentifyingNounPhrase[T] extends SingleIdentifyingNounPhrase[T] {
  def identify(gameState: GameState, effectContext: EffectContext): T

  override def identifySingle(gameState: GameState, resolutionContext: StackObjectResolutionContext): (T, StackObjectResolutionContext) = {
    (identify(gameState, resolutionContext), resolutionContext)
  }
}
