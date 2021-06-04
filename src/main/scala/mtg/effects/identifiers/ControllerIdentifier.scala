package mtg.effects.identifiers

import mtg.effects.StackObjectResolutionContext
import mtg.game.state.{GameState, PermanentObjectWithState, StackObjectWithState}
import mtg.game.{ObjectId, PlayerId}

case class ControllerIdentifier(objectIdentifier: Identifier[ObjectId]) extends Identifier[PlayerId] {
  override def get(gameState: GameState, resolutionContext: StackObjectResolutionContext): (PlayerId, StackObjectResolutionContext) = {
    val (objectId, resolutionContextAfterObject) = objectIdentifier.get(gameState, resolutionContext)
    val controller = gameState.gameObjectState.getCurrentOrLastKnownState(objectId) match {
      case Some(permanentObjectWithState: PermanentObjectWithState) =>
        permanentObjectWithState.controller
      case Some(stackObjectWithState: StackObjectWithState) =>
        stackObjectWithState.controller
      case _ =>
        throw new Exception(s"$objectId has no controller")
    }
    (controller, resolutionContextAfterObject)
  }

  override def getText(cardName: String): String = objectIdentifier.getPossessiveText(cardName) + " controller"
}
