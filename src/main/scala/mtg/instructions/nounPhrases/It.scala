package mtg.instructions.nounPhrases

import mtg.definitions.ObjectId
import mtg.instructions.InstructionAction
import mtg.instructions.grammar.GrammaticalPerson

case object It extends SingleIdentifyingNounPhrase[ObjectId] {
  override def getText(cardName: String): String = "it"
  override def getPossessiveText(cardName: String): String = "its"
  override def person: GrammaticalPerson = GrammaticalPerson.Third
  def identifySingle: InstructionAction.WithResult[ObjectId] = InstructionAction.WithResult.withoutContextUpdate { (resolutionContext, _) =>
    resolutionContext.identifiedObjects.last.asInstanceOf[ObjectId]
  }
}
