package mtg.effects.identifiers

import mtg.effects.ResolutionContext
import mtg.game.{ObjectId, PlayerId}
import mtg.game.state.GameState

object YouIdentifier extends Identifier[PlayerId] {
  override def get(gameState: GameState, resolutionContext: ResolutionContext): (PlayerId, ResolutionContext) = {
    (resolutionContext.controller, resolutionContext)
  }
  override def getText(cardName: String): String = "you"
}
