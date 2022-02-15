package mtg.effects.oneshot.basic

import mtg.effects.identifiers.SingleIdentifier
import mtg.effects.oneshot.OneShotEffectResult
import mtg.effects.{OneShotEffect, StackObjectResolutionContext}
import mtg.events.moveZone.MoveToHandEvent
import mtg.game.ObjectId
import mtg.game.state.GameState

case class PutIntoHandEffect(objectIdentifier: SingleIdentifier[ObjectId]) extends OneShotEffect {
  override def getText(cardName: String): String = s"put ${objectIdentifier.getText(cardName)} into your hand"

  override def resolve(gameState: GameState, resolutionContext: StackObjectResolutionContext): OneShotEffectResult = {
    val (objectId, contextAfterObject) = objectIdentifier.get(gameState, resolutionContext)
    (MoveToHandEvent(objectId), contextAfterObject)
  }
}
