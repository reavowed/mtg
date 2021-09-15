package mtg.events

import mtg.game.ObjectId
import mtg.game.state.{InternalGameAction, GameActionResult, GameState}

case class RemoveObjectFromExistenceEvent(objectId: ObjectId) extends InternalGameAction {
  override def execute(gameState: GameState): GameActionResult = {
    gameState.gameObjectState.derivedState.allObjectStates.get(objectId).map(gameObjectWithState => {
      gameState.gameObjectState.deleteObject(gameObjectWithState.gameObject)
    })
  }
  override def canBeReverted: Boolean = true
}
