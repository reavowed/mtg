package mtg.effects.oneshot.basic

import mtg.effects.oneshot.OneShotEffectResult
import mtg.effects.{OneShotEffect, StackObjectResolutionContext}
import mtg.events.DrawCardEvent
import mtg.game.state.GameState

case object DrawACardEffect extends OneShotEffect {
  override def getText(cardName: String): String = "draw a card"
  override def resolve(gameState: GameState, resolutionContext: StackObjectResolutionContext): OneShotEffectResult = {
    (DrawCardEvent(resolutionContext.controllingPlayer), resolutionContext)
  }
}
