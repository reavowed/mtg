package mtg.effects.oneshot.basic

import mtg.core.PlayerId
import mtg.effects.identifiers.SingleIdentifier
import mtg.effects.oneshot.OneShotEffectResult
import mtg.effects.{OneShotEffect, StackObjectResolutionContext}
import mtg.actions.DrawCardAction
import mtg.game.state.GameState

case class DrawsACardEffect(playerIdentifier: SingleIdentifier[PlayerId]) extends OneShotEffect {
  override def getText(cardName: String): String = playerIdentifier.getText(cardName) + " draws a card"
  override def resolve(gameState: GameState, resolutionContext: StackObjectResolutionContext): OneShotEffectResult = {
    playerIdentifier.get(gameState, resolutionContext).mapLeft(DrawCardAction)
  }
}
