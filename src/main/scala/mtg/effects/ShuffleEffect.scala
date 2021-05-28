package mtg.effects

import mtg.events.shuffle.ShuffleLibrary
import mtg.game.state.GameState

object ShuffleEffect extends Effect {
  override def text: String = "shuffle"
  override def resolve(gameState: GameState, resolutionContext: ResolutionContext): EffectResult = {
    ShuffleLibrary(resolutionContext.controller)
  }
}
