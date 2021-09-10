package mtg.events

import mtg.game.ObjectId
import mtg.game.state.{GameObjectAction, GameObjectActionResult, GameState}

case class RemoveObjectFromExistenceAction(objectId: ObjectId) extends GameObjectAction {
  override def execute(currentGameState: GameState): GameObjectActionResult = {
    currentGameState.gameObjectState.derivedState.allObjectStates.get(objectId).map(gameObjectWithState => {
      currentGameState.gameObjectState.deleteObject(gameObjectWithState.gameObject)
    })
  }
}
