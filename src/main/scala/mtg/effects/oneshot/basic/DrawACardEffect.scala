package mtg.effects.oneshot.basic

import mtg.effects.OneShotEffect
import mtg.effects.identifiers.Identifier
import mtg.effects.oneshot.{OneShotEffectResolutionContext, OneShotEffectResult}
import mtg.events.DrawCardEvent
import mtg.game.PlayerId
import mtg.game.state.GameState

case object DrawACardEffect extends OneShotEffect {
  override def getText(cardName: String): String = "draw a card"
  override def resolve(gameState: GameState, resolutionContext: OneShotEffectResolutionContext): OneShotEffectResult = {
    (DrawCardEvent(resolutionContext.controller), resolutionContext)
  }
}
