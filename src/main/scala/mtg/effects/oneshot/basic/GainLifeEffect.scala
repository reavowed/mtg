package mtg.effects.oneshot.basic

import mtg.effects.{EffectContext, OneShotEffect, StackObjectResolutionContext}
import mtg.effects.identifiers.Identifier
import mtg.effects.oneshot.OneShotEffectResult
import mtg.events.GainLifeAction
import mtg.game.PlayerId
import mtg.game.state.GameState

case class GainLifeEffect(playerIdentifier: Identifier[PlayerId], amount: Int) extends OneShotEffect {
  override def getText(cardName: String): String = s"you gain $amount life"

  override def resolve(gameState: GameState, resolutionContext: StackObjectResolutionContext): OneShotEffectResult = {
    val (player, finalResolutionContext) = playerIdentifier.get(gameState, resolutionContext)
    (GainLifeAction(player, amount), finalResolutionContext)
  }
}
