package mtg.effects.oneshot.basic

import mtg.effects.oneshot.{OneShotEffect, OneShotEffectResolutionContext, OneShotEffectResult}
import mtg.events.shuffle.ShuffleLibrary
import mtg.game.state.GameState

case object ShuffleEffect extends OneShotEffect {
  override def getText(cardName: String): String = "shuffle"

  override def resolve(gameState: GameState, resolutionContext: OneShotEffectResolutionContext): OneShotEffectResult = {
    (ShuffleLibrary(resolutionContext.controller), resolutionContext)
  }
}
