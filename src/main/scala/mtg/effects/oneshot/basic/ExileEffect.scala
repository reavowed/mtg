package mtg.effects.oneshot.basic

import mtg.effects.OneShotEffect
import mtg.effects.identifiers.Identifier
import mtg.effects.oneshot.{OneShotEffectResolutionContext, OneShotEffectResult}
import mtg.events.MoveObjectEvent
import mtg.game.state.GameState
import mtg.game.{ObjectId, Zone}

case class ExileEffect(objectIdentifier: Identifier[ObjectId]) extends OneShotEffect {
  override def getText(cardName: String): String = s"exile ${objectIdentifier.getText(cardName)}"

  override def resolve(gameState: GameState, resolutionContext: OneShotEffectResolutionContext): OneShotEffectResult = {
    val player = resolutionContext.controller
    val (obj, contextAfterObject) = objectIdentifier.get(gameState, resolutionContext)
    (MoveObjectEvent(player, obj, Zone.Exile), contextAfterObject)
  }
}
