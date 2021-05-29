package mtg.effects

import mtg.effects.identifiers.Identifier
import mtg.events.MoveObjectEvent
import mtg.game.state.GameState
import mtg.game.{ObjectId, Zone}

case class PutIntoHandEffect(objectIdentifier: Identifier[ObjectId]) extends Effect {
  override def getText(cardName: String): String = s"put ${objectIdentifier.getText(cardName)} into your hand"
  override def resolve(gameState: GameState, resolutionContext: ResolutionContext): EffectResult = {
    val player = resolutionContext.controller
    val (obj, contextAfterObject) = objectIdentifier.get(gameState, resolutionContext)
    (MoveObjectEvent(player, obj, Zone.Hand(player)), contextAfterObject)
  }
}
