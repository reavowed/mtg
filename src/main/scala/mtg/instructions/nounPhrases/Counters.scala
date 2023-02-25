package mtg.instructions.nounPhrases

import mtg.definitions.ObjectId
import mtg.instructions.InstructionAction
import mtg.instructions.grammar.GrammaticalPerson
import mtg.parts.Counter

case class Counters(objectPhrase: SingleIdentifyingNounPhrase[ObjectId]) extends SingleIdentifyingNounPhrase[Map[Counter, Int]] {
  override def getText(cardName: String): String = objectPhrase.getPossessiveText(cardName) + " counters"
  override def person: GrammaticalPerson = GrammaticalPerson.Third
  override def identifySingle: InstructionAction.WithResult[Map[Counter, Int]] = {
    objectPhrase.identifySingle.flatMap(getCounters)
  }
  private def getCounters(objectId: ObjectId): InstructionAction.WithResult[Map[Counter, Int]] = InstructionAction.WithResult.withoutContextUpdate { gameState =>
    gameState.gameObjectState.getCurrentOrLastKnownState(objectId).gameObject.counters
  }
}
