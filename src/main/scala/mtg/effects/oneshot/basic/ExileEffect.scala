package mtg.effects.oneshot.basic

import mtg.effects.identifiers.SingleIdentifier
import mtg.effects.oneshot.OneShotEffectResult
import mtg.effects.{OneShotEffect, StackObjectResolutionContext}
import mtg.events.moveZone.MoveToExileEvent
import mtg.game.ObjectId
import mtg.game.state.GameState

case class ExileEffect(objectIdentifier: SingleIdentifier[ObjectId]) extends OneShotEffect {
  override def getText(cardName: String): String = s"exile ${objectIdentifier.getText(cardName)}"

  override def resolve(gameState: GameState, resolutionContext: StackObjectResolutionContext): OneShotEffectResult = {
    val (objectId, contextAfterObject) = objectIdentifier.get(gameState, resolutionContext)
    (MoveToExileEvent(objectId), contextAfterObject)
  }
}
