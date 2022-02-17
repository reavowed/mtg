package mtg.actions.moveZone

import mtg.core.ObjectId
import mtg.game.objects.{GameObject, GameObjectState}
import mtg.game.state.{GameActionResult, GameState, InternalGameAction, ObjectWithState}

abstract class MoveObjectAction[TGameObject <: GameObject] extends InternalGameAction {
  def objectId: ObjectId
  def createNewObject(existingObjectWithState: ObjectWithState, newObjectId: ObjectId): TGameObject
  def addGameObjectToState(existingObjectWithState: ObjectWithState, gameObjectState: GameObjectState, objectConstructor: ObjectId => TGameObject): GameObjectState

  def execute(gameState: GameState): GameActionResult = {
    gameState.gameObjectState.derivedState.allObjectStates.get(objectId).map(existingObjectWithState => {
      addGameObjectToState(
        existingObjectWithState,
        gameState.gameObjectState.deleteObject(existingObjectWithState.gameObject),
        createNewObject(existingObjectWithState, _))
    })
  }
  // TODO: figure out how to make this revertible if the previous card was not hidden
  override def canBeReverted: Boolean = false
}
