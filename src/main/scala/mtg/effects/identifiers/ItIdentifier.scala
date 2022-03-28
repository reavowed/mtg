package mtg.effects.identifiers

import mtg.core.ObjectId
import mtg.effects.StackObjectResolutionContext
import mtg.game.state.GameState
import mtg.text.VerbPerson

object ItIdentifier extends SingleIdentifier[ObjectId] {
  override def identifySingle(gameState: GameState, resolutionContext: StackObjectResolutionContext): (ObjectId, StackObjectResolutionContext) = {
    (resolutionContext.identifiedObjects.last.asInstanceOf[ObjectId], resolutionContext)
  }
  override def getText(cardName: String): String = "it"
  override def person: VerbPerson = VerbPerson.Third
}
