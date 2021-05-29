package mtg.effects.identifiers

import mtg.effects.ResolutionContext
import mtg.game.ObjectId
import mtg.game.state.GameState

object ThisIdentifier extends Identifier[ObjectId] {
  override def get(gameState: GameState, resolutionContext: ResolutionContext): (ObjectId, ResolutionContext) = {
    (resolutionContext.source, resolutionContext.addObject(resolutionContext.source))
  }
  override def getText(cardName: String): String = cardName
}
