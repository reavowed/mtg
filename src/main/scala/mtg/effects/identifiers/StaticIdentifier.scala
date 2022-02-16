package mtg.effects.identifiers

import mtg.core.ObjectOrPlayerId
import mtg.effects.{EffectContext, StackObjectResolutionContext}
import mtg.game.state.GameState

trait StaticIdentifier[+T <: ObjectOrPlayerId] extends SingleIdentifier[T] {
  def get(effectContext: EffectContext, gameState: GameState): T
  def get(gameState: GameState, resolutionContext: StackObjectResolutionContext): (T, StackObjectResolutionContext) = {
    (get(resolutionContext, gameState), resolutionContext)
  }
}
