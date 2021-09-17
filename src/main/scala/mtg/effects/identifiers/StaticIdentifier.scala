package mtg.effects.identifiers

import mtg.effects.{EffectContext, StackObjectResolutionContext}
import mtg.game.ObjectOrPlayer
import mtg.game.state.GameState

trait StaticIdentifier[+T <: ObjectOrPlayer] extends SingleIdentifier[T] {
  def get(effectContext: EffectContext, gameState: GameState): T
  def get(gameState: GameState, resolutionContext: StackObjectResolutionContext): (T, StackObjectResolutionContext) = {
    (get(resolutionContext, gameState), resolutionContext)
  }
}
