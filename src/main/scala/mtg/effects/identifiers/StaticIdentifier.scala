package mtg.effects.identifiers

import mtg.core.ObjectOrPlayerId
import mtg.effects.{EffectContext, StackObjectResolutionContext}
import mtg.game.state.GameState

trait StaticIdentifier[+T <: ObjectOrPlayerId] extends SingleIdentifier[T] {
  def get(gameState: GameState, effectContext: EffectContext): T
  def identifySingle(gameState: GameState, resolutionContext: StackObjectResolutionContext): (T, StackObjectResolutionContext) = {
    (get(gameState, resolutionContext), resolutionContext)
  }
}
