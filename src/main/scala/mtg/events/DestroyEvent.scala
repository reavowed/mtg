package mtg.events

import mtg.game.{PlayerIdentifier, Zone}
import mtg.game.objects.ObjectId
import mtg.game.state.{GameObjectEvent, GameObjectEventResult, GameState}

case class DestroyEvent(player: PlayerIdentifier, objectId: ObjectId) extends GameObjectEvent {
  override def execute(currentGameState: GameState): GameObjectEventResult = {
    currentGameState.gameObjectState.allObjects.find(_.objectId == objectId).map(gameObject => {
      MoveObjectEvent(player, objectId, Zone.Graveyard(gameObject.owner))
    })
  }
}
