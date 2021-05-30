package mtg.effects.oneshot.basic

import mtg.effects.identifiers.Identifier
import mtg.effects.oneshot.{OneShotEffect, OneShotEffectResolutionContext, OneShotEffectResult}
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
