package mtg.instructions.nouns

import mtg.core.{ObjectId, PlayerId}
import mtg.effects.StackObjectResolutionContext
import mtg.game.state.GameState
import mtg.text.{VerbNumber, VerbPerson}

case class Controller(objectNoun: SingleIdentifyingNounPhrase[ObjectId]) extends SingleIdentifyingNounPhrase[PlayerId] {
  override def getText(cardName: String): String = objectNoun.getPossessiveText(cardName) + " controller"
  override def person: VerbPerson = VerbPerson.Third
  override def number: VerbNumber = VerbNumber.Singular
  override def identify(gameState: GameState, resolutionContext: StackObjectResolutionContext): (PlayerId, StackObjectResolutionContext)  = {
    val (objectId, resolutionContextAfterObject) = objectNoun.identify(gameState, resolutionContext)
    val controller = gameState.gameObjectState.getCurrentOrLastKnownState(objectId).controllerOrOwner
    (controller, resolutionContextAfterObject)
  }
}
