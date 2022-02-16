package mtg.effects.oneshot.basic

import mtg.core.{ObjectId, ObjectOrPlayerId}
import mtg.effects.identifiers.SingleIdentifier
import mtg.effects.oneshot.OneShotEffectResult
import mtg.effects.{OneShotEffect, StackObjectResolutionContext}
import mtg.game.state.GameState
import mtg.parts.damage.DealDamageEvent

case class DealDamageEffect(sourceIdentifier: SingleIdentifier[ObjectId], recipientIdentifier: SingleIdentifier[ObjectOrPlayerId], amount: Int) extends OneShotEffect {
  override def getText(cardName: String): String = sourceIdentifier.getText(cardName) + s" deals $amount damage to " + recipientIdentifier.getText(cardName)

  override def resolve(gameState: GameState, resolutionContext: StackObjectResolutionContext): OneShotEffectResult = {
    val (source, contextAfterSource) = sourceIdentifier.get(gameState, resolutionContext)
    val (recipient, contextAfterRecipient) = recipientIdentifier.get(gameState, contextAfterSource)
    (DealDamageEvent(source, recipient, amount), contextAfterRecipient)
  }
}
