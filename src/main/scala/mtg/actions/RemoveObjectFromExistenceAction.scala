package mtg.actions

import mtg.core.ObjectId
import mtg.game.objects.GameObjectState
import mtg.game.state.{DirectGameObjectAction, GameState}

case class RemoveObjectFromExistenceAction(objectId: ObjectId) extends DirectGameObjectAction {
  override def execute(implicit gameState: GameState): GameObjectState = {
    gameState.gameObjectState.derivedState.allObjectStates.get(objectId).map(gameObjectWithState => {
      gameState.gameObjectState.deleteObject(gameObjectWithState.gameObject)
    })
  }
  override def canBeReverted: Boolean = true
}
