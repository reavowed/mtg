package mtg.effects.oneshot.basic

import mtg.effects.{EffectContext, OneShotEffect, StackObjectResolutionContext}
import mtg.effects.identifiers.Identifier
import mtg.effects.oneshot.OneShotEffectResult
import mtg.events.DrawCardAction
import mtg.game.PlayerId
import mtg.game.state.GameState

case class DrawsACardEffect(playerIdentifier: Identifier[PlayerId]) extends OneShotEffect {
  override def getText(cardName: String): String = playerIdentifier.getText(cardName) + " draws a card"
  override def resolve(gameState: GameState, resolutionContext: StackObjectResolutionContext): OneShotEffectResult = {
    playerIdentifier.get(gameState, resolutionContext).mapLeft(DrawCardAction)
  }
}
