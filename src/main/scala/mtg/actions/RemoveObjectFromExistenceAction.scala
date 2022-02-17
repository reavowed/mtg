package mtg.actions

import mtg.core.ObjectId
import mtg.game.state.{GameActionResult, GameState, InternalGameAction}

case class RemoveObjectFromExistenceAction(objectId: ObjectId) extends InternalGameAction {
  override def execute(gameState: GameState): GameActionResult = {
    gameState.gameObjectState.derivedState.allObjectStates.get(objectId).map(gameObjectWithState => {
      gameState.gameObjectState.deleteObject(gameObjectWithState.gameObject)
    })
  }
  override def canBeReverted: Boolean = true
}
