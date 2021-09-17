package mtg.effects.identifiers

import mtg.effects.StackObjectResolutionContext
import mtg.game.state.GameState
import mtg.game.{ObjectId, PlayerId}

case class ControllerIdentifier(objectIdentifier: SingleIdentifier[ObjectId]) extends SingleIdentifier[PlayerId] {
  override def get(gameState: GameState, resolutionContext: StackObjectResolutionContext): (PlayerId, StackObjectResolutionContext) = {
    val (objectId, resolutionContextAfterObject) = objectIdentifier.get(gameState, resolutionContext)
    val controller = gameState.gameObjectState.getCurrentOrLastKnownState(objectId).controllerOrOwner
    (controller, resolutionContextAfterObject)
  }

  override def getText(cardName: String): String = objectIdentifier.getPossessiveText(cardName) + " controller"
}
