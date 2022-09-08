package mtg.actions

import mtg.definitions.ObjectId
import mtg.game.state.{DirectGameObjectAction, GameState}

case class RemoveObjectFromExistenceAction(objectId: ObjectId) extends DirectGameObjectAction[Unit] {
  override def execute(implicit gameState: GameState): DirectGameObjectAction.Result[Unit] = {
    gameState.gameObjectState.derivedState.allObjectStates.get(objectId).map(gameObjectWithState => {
      gameState.gameObjectState.deleteObject(gameObjectWithState.gameObject)
    })
  }
  override def canBeReverted: Boolean = true
}
