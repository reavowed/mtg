package mtg.effects.identifiers

import mtg.core.{ObjectId, PlayerId}
import mtg.effects.StackObjectResolutionContext
import mtg.game.state.GameState
import mtg.text.VerbPerson

case class ControllerIdentifier(objectIdentifier: SingleIdentifier[ObjectId]) extends SingleIdentifier[PlayerId] {
  override def identifySingle(gameState: GameState, resolutionContext: StackObjectResolutionContext): (PlayerId, StackObjectResolutionContext) = {
    val (objectId, resolutionContextAfterObject) = objectIdentifier.identifySingle(gameState, resolutionContext)
    val controller = gameState.gameObjectState.getCurrentOrLastKnownState(objectId).controllerOrOwner
    (controller, resolutionContextAfterObject)
  }
  override def getText(cardName: String): String = {
    objectIdentifier.getPossessiveText(cardName) + " controller"
  }
  override def person: VerbPerson = VerbPerson.Third
}
