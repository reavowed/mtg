package mtg.events.moveZone

import mtg.game.ObjectId
import mtg.game.objects.GameObject
import mtg.game.state.{GameActionResult, GameState, InternalGameAction, ObjectWithState}

abstract class MoveObjectEvent[T <: GameObject] extends InternalGameAction {
  def objectId: ObjectId
  def createNewObject(existingObjectWithState: ObjectWithState, newObjectId: ObjectId): T

  def execute(gameState: GameState): GameActionResult = {
    gameState.gameObjectState.derivedState.allObjectStates.get(objectId).map(existingObjectWithState => {
        // TODO: Handle putting something onto the battlefield under another player's control
      gameState.gameObjectState
        .deleteObject(existingObjectWithState.gameObject)
        .addNewObject(createNewObject(existingObjectWithState, _), _.length)
    })
  }
  // TODO: figure out how to make this revertible if the previous card was not hidden
  override def canBeReverted: Boolean = false
}
