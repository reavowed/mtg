package mtg.effects.oneshot.basic

import mtg.effects.OneShotEffect
import mtg.effects.identifiers.Identifier
import mtg.effects.oneshot.{OneShotEffectResolutionContext, OneShotEffectResult}
import mtg.events.DrawCardEvent
import mtg.game.PlayerId
import mtg.game.state.GameState

case class DrawACardEffect(playerIdentifier: Identifier[PlayerId]) extends OneShotEffect {
  override def getText(cardName: String): String = playerIdentifier.getText(cardName) + " draws a card"
  override def resolve(gameState: GameState, resolutionContext: OneShotEffectResolutionContext): OneShotEffectResult = {
    playerIdentifier.get(gameState, resolutionContext).mapLeft(DrawCardEvent)
  }
}
