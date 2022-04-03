package mtg.instructions.nounPhrases

import mtg.core.{ObjectId, PlayerId}
import mtg.effects.StackObjectResolutionContext
import mtg.game.state.GameState
import mtg.instructions.VerbPerson

case class Controller(objectNoun: SingleIdentifyingNounPhrase[ObjectId]) extends SingleIdentifyingNounPhrase[PlayerId] {
  override def getText(cardName: String): String = objectNoun.getPossessiveText(cardName) + " controller"

  override def person: VerbPerson = VerbPerson.Third

  override def identifySingle(gameState: GameState, resolutionContext: StackObjectResolutionContext): (PlayerId, StackObjectResolutionContext) = {
    val (objectId, resolutionContextAfterObject) = objectNoun.identifySingle(gameState, resolutionContext)
    val controller = gameState.gameObjectState.getCurrentOrLastKnownState(objectId).controllerOrOwner
    (controller, resolutionContextAfterObject)
  }
}
