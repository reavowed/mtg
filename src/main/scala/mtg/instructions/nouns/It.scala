package mtg.instructions.nouns

import mtg.core.ObjectId
import mtg.effects.StackObjectResolutionContext
import mtg.game.state.GameState
import mtg.text.{VerbNumber, VerbPerson}

case object It extends SingleIdentifyingNounPhrase[ObjectId] {
  override def getText(cardName: String): String = "it"
  override def getPossessiveText(cardName: String): String = "its"
  override def person: VerbPerson = VerbPerson.Third
  override def number: VerbNumber = VerbNumber.Singular
  override def identify(gameState: GameState, resolutionContext: StackObjectResolutionContext): (ObjectId, StackObjectResolutionContext) = {
    (resolutionContext.identifiedObjects.last.asInstanceOf[ObjectId], resolutionContext)
  }
}