package mtg.events

import mtg.game.{ObjectId, PlayerId, Zone}
import mtg.game.state.{GameObjectAction, GameObjectActionResult, GameState}

case class DestroyAction(player: PlayerId, objectId: ObjectId) extends GameObjectAction {
  override def execute(currentGameState: GameState): GameObjectActionResult = {
    currentGameState.gameObjectState.allObjects.find(_.objectId == objectId).map(gameObject => {
      MoveObjectAction(player, objectId, Zone.Graveyard(gameObject.owner))
    })
  }
}
