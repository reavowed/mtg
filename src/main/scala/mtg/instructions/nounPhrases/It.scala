package mtg.instructions.nounPhrases

import mtg.core.ObjectId
import mtg.effects.InstructionResolutionContext
import mtg.game.state.GameState
import mtg.instructions.grammar.GrammaticalPerson

case object It extends SingleIdentifyingNounPhrase[ObjectId] {
  override def getText(cardName: String): String = "it"
  override def getPossessiveText(cardName: String): String = "its"
  override def person: GrammaticalPerson = GrammaticalPerson.Third
  override def identifySingle(gameState: GameState, resolutionContext: InstructionResolutionContext): (ObjectId, InstructionResolutionContext) = {
    (resolutionContext.identifiedObjects.last.asInstanceOf[ObjectId], resolutionContext)
  }
}
