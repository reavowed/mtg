package mtg.instructions.nounPhrases

import mtg.definitions.{ObjectId, PlayerId}
import mtg.effects.InstructionResolutionContext
import mtg.game.state.GameState
import mtg.instructions.grammar.GrammaticalPerson

case class Controller(objectNoun: SingleIdentifyingNounPhrase[ObjectId]) extends SingleIdentifyingNounPhrase[PlayerId] {
  override def getText(cardName: String): String = objectNoun.getPossessiveText(cardName) + " controller"

  override def person: GrammaticalPerson = GrammaticalPerson.Third

  override def identifySingle(gameState: GameState, resolutionContext: InstructionResolutionContext): (PlayerId, InstructionResolutionContext) = {
    val (objectId, resolutionContextAfterObject) = objectNoun.identifySingle(gameState, resolutionContext)
    val controller = gameState.gameObjectState.getCurrentOrLastKnownState(objectId).controllerOrOwner
    (controller, resolutionContextAfterObject)
  }
}
