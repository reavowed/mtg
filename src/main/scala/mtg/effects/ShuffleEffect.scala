package mtg.effects

import mtg.events.shuffle.ShuffleLibrary
import mtg.game.state.GameState

case object ShuffleEffect extends Effect {
  override def getText(cardName: String): String = "shuffle"
  override def resolve(gameState: GameState, resolutionContext: ResolutionContext): EffectResult = {
    (ShuffleLibrary(resolutionContext.controller), resolutionContext)
  }
}
