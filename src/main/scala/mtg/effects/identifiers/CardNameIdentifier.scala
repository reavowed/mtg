package mtg.effects.identifiers

import mtg.core.ObjectId
import mtg.effects.StackObjectResolutionContext
import mtg.game.state.GameState
import mtg.text.VerbPerson

object CardNameIdentifier extends SingleIdentifier[ObjectId] {
  override def identifySingle(gameState: GameState, resolutionContext: StackObjectResolutionContext): (ObjectId, StackObjectResolutionContext) = {
    (resolutionContext.sourceId, resolutionContext.addIdentifiedObject(resolutionContext.sourceId))
  }
  override def getText(cardName: String): String = cardName
  override def person: VerbPerson = VerbPerson.Third
}
