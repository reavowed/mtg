package mtg.events

import mtg.game.ObjectId
import mtg.game.state.{GameObjectEvent, GameObjectEventResult, GameState}

case class RemoveObjectFromExistenceEvent(objectId: ObjectId) extends GameObjectEvent {
  override def execute(currentGameState: GameState): GameObjectEventResult = {
    currentGameState.gameObjectState.derivedState.allObjectStates.get(objectId).map(gameObjectWithState => {
      currentGameState.gameObjectState.deleteObject(gameObjectWithState.gameObject)
    })
  }
}
