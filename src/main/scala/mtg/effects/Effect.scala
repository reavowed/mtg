package mtg.effects

import mtg.game.state.GameState

abstract class Effect {
  def text: String
  def resolve(gameState: GameState, resolutionContext: ResolutionContext): EffectResult
}
