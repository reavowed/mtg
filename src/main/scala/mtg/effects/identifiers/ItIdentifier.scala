package mtg.effects.identifiers

import mtg.effects.StackObjectResolutionContext
import mtg.game.ObjectId
import mtg.game.state.GameState
import mtg.text.{NounPhrase, NounPhrases}

object ItIdentifier extends SingleIdentifier[ObjectId] {
  override def get(gameState: GameState, resolutionContext: StackObjectResolutionContext): (ObjectId, StackObjectResolutionContext) = {
    (resolutionContext.identifiedObjects.last.asInstanceOf[ObjectId], resolutionContext)
  }

  override def getNounPhrase(cardName: String): NounPhrase = NounPhrases.It
}
