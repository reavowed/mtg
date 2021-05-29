package mtg.events

import mtg.game.{ObjectId, PlayerId, Zone}
import mtg.game.state.{GameObjectEvent, GameObjectEventResult, GameState}

case class DestroyEvent(player: PlayerId, objectId: ObjectId) extends GameObjectEvent {
  override def execute(currentGameState: GameState): GameObjectEventResult = {
    currentGameState.gameObjectState.allObjects.find(_.objectId == objectId).map(gameObject => {
      MoveObjectEvent(player, objectId, Zone.Graveyard(gameObject.owner))
    })
  }
}
