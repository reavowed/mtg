package mtg.effects.oneshot.actions

import mtg.effects.oneshot.OneShotEffectResult
import mtg.effects.{OneShotEffect, StackObjectResolutionContext}
import mtg.events.shuffle.ShuffleLibrary
import mtg.game.state.GameState

case object ShuffleEffect extends OneShotEffect {
  override def getText(cardName: String): String = "shuffle"

  override def resolve(gameState: GameState, resolutionContext: StackObjectResolutionContext): OneShotEffectResult = {
    (ShuffleLibrary(resolutionContext.controllingPlayer), resolutionContext)
  }
}
