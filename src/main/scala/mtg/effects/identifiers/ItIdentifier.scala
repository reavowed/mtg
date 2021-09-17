package mtg.effects.identifiers

import mtg.effects.StackObjectResolutionContext
import mtg.game.ObjectId
import mtg.game.state.GameState

object ItIdentifier extends SingleIdentifier[ObjectId] {
  override def get(gameState: GameState, resolutionContext: StackObjectResolutionContext): (ObjectId, StackObjectResolutionContext) = {
    (resolutionContext.identifiedObjects.last.asInstanceOf[ObjectId], resolutionContext)
  }
  override def getText(cardName: String): String = "it"
  override def getPossessiveText(cardName: String): String = "its"
}
