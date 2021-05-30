package mtg.effects.oneshot.basic

import mtg.effects.identifiers.Identifier
import mtg.effects.oneshot.{OneShotEffect, OneShotEffectResolutionContext, OneShotEffectResult}
import mtg.game.state.GameState
import mtg.game.{ObjectId, ObjectOrPlayer}
import mtg.parts.damage.DealDamageEvent

case class DealDamageEffect(sourceIdentifier: Identifier[ObjectId], recipientIdentifier: Identifier[ObjectOrPlayer], amount: Int) extends OneShotEffect {
  override def getText(cardName: String): String = sourceIdentifier.getText(cardName) + s" deals $amount damage to " + recipientIdentifier.getText(cardName)

  override def resolve(gameState: GameState, resolutionContext: OneShotEffectResolutionContext): OneShotEffectResult = {
    val (source, contextAfterSource) = sourceIdentifier.get(gameState, resolutionContext)
    val (recipient, contextAfterRecipient) = recipientIdentifier.get(gameState, contextAfterSource)
    (DealDamageEvent(source, recipient, amount), contextAfterRecipient)
  }
}
