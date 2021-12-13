package mtg.effects.oneshot.basic

import mtg.effects.identifiers.SingleIdentifier
import mtg.effects.oneshot.OneShotEffectResult
import mtg.effects.{OneShotEffect, StackObjectResolutionContext}
import mtg.events.DestroyEvent
import mtg.game.ObjectId
import mtg.game.state.GameState

case class DestroyEffect(objectIdentifier: SingleIdentifier[ObjectId]) extends OneShotEffect {
  override def getText(cardName: String): String = "Destroy " + objectIdentifier.getText(cardName)
  override def resolve(gameState: GameState, resolutionContext: StackObjectResolutionContext): OneShotEffectResult = {
    val (o, contextAfterObject) = objectIdentifier.get(gameState, resolutionContext)
    (DestroyEvent(resolutionContext.controllingPlayer, o), contextAfterObject)
  }
}
