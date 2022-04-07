package mtg.actions.moveZone

import mtg.core.ObjectId
import mtg.game.objects.{GameObject, GameObjectState}
import mtg.game.state.{DirectGameObjectAction, GameState, ObjectWithState}

abstract class MoveObjectAction[TGameObject <: GameObject] extends DirectGameObjectAction {
  def objectId: ObjectId
  def createNewObject(existingObjectWithState: ObjectWithState, newObjectId: ObjectId): TGameObject
  def addGameObjectToState(existingObjectWithState: ObjectWithState, gameObjectState: GameObjectState, objectConstructor: ObjectId => TGameObject): GameObjectState

  override def execute(implicit gameState: GameState): GameObjectState = {
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
