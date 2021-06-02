package mtg.effects.oneshot.actions

import mtg.effects.OneShotEffect
import mtg.effects.identifiers.Identifier
import mtg.effects.oneshot.{OneShotEffectResolutionContext, OneShotEffectResult}
import mtg.game.ObjectId
import mtg.game.state.GameState
import mtg.game.state.history.LogEvent

case class RevealEffect(objectIdentifier: Identifier[ObjectId]) extends OneShotEffect {
  override def getText(cardName: String): String = "reveal " + objectIdentifier.getText(cardName)

  override def resolve(gameState: GameState, resolutionContext: OneShotEffectResolutionContext): OneShotEffectResult = {
    val (obj, contextAfterObject) = objectIdentifier.get(gameState, resolutionContext)
    (LogEvent.RevealCard(resolutionContext.controller, obj.getName(gameState)), contextAfterObject)
  }
}
