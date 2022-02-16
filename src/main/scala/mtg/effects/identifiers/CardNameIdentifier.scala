package mtg.effects.identifiers

import mtg.core.ObjectId
import mtg.effects.StackObjectResolutionContext
import mtg.game.state.GameState
import mtg.text.{GrammaticalNumber, NounPhrase}

object CardNameIdentifier extends SingleIdentifier[ObjectId] {
  override def get(gameState: GameState, resolutionContext: StackObjectResolutionContext): (ObjectId, StackObjectResolutionContext) = {
    (resolutionContext.sourceId, resolutionContext.addIdentifiedObject(resolutionContext.sourceId))
  }
  override def getNounPhrase(cardName: String): NounPhrase = NounPhrase.Simple(cardName, GrammaticalNumber.Singular)
}
