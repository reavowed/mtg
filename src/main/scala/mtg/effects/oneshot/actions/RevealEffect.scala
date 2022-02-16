package mtg.effects.oneshot.actions

import mtg.effects.identifiers.SingleIdentifier
import mtg.effects.oneshot.OneShotEffectResult
import mtg.effects.{OneShotEffect, StackObjectResolutionContext}
import mtg.game.ObjectId
import mtg.game.state.{CurrentCharacteristics, GameState}
import mtg.game.state.history.LogEvent

case class RevealEffect(objectIdentifier: SingleIdentifier[ObjectId]) extends OneShotEffect {
  override def getText(cardName: String): String = "reveal " + objectIdentifier.getText(cardName)

  override def resolve(gameState: GameState, resolutionContext: StackObjectResolutionContext): OneShotEffectResult = {
    val (objectId, contextAfterObject) = objectIdentifier.get(gameState, resolutionContext)
    (LogEvent.RevealCard(resolutionContext.controllingPlayer, CurrentCharacteristics.getName(objectId, gameState)), contextAfterObject)
  }
}
