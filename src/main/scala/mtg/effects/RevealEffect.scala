package mtg.effects

import mtg.effects.identifiers.Identifier
import mtg.game.ObjectId
import mtg.game.state.GameState
import mtg.game.state.history.LogEvent

case class RevealEffect(objectIdentifier: Identifier[ObjectId]) extends Effect {
  override def getText(cardName: String): String = "reveal " + objectIdentifier.getText(cardName)
  override def resolve(gameState: GameState, resolutionContext: ResolutionContext): EffectResult = {
    val (obj, contextAfterObject) = objectIdentifier.get(gameState, resolutionContext)
    (LogEvent.RevealCard(resolutionContext.controller, obj.getName(gameState)), contextAfterObject)
  }
}
