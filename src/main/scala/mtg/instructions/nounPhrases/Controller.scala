package mtg.instructions.nounPhrases

import mtg.definitions.{ObjectId, PlayerId}
import mtg.instructions.InstructionAction
import mtg.instructions.grammar.GrammaticalPerson

case class Controller(objectNoun: SingleIdentifyingNounPhrase[ObjectId]) extends SingleIdentifyingNounPhrase[PlayerId] {
  override def getText(cardName: String): String = objectNoun.getPossessiveText(cardName) + " controller"

  override def person: GrammaticalPerson = GrammaticalPerson.Third

  override def identifySingle: InstructionAction.WithResult[PlayerId] = {
    objectNoun.identifySingle.flatMap(getController)
  }

  private def getController(objectId: ObjectId): InstructionAction.WithResult[PlayerId] = InstructionAction.WithResult.withoutContextUpdate { gameState =>
    gameState.gameObjectState.getCurrentOrLastKnownState(objectId).controllerOrOwner
  }
}
