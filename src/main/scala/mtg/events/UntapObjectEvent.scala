package mtg.events

import mtg.game.objects.ObjectId
import mtg.game.state.{GameObjectEvent, GameObjectEventResult, GameState}

case class UntapObjectEvent(objectId: ObjectId) extends GameObjectEvent {
  override def execute(currentGameState: GameState): GameObjectEventResult = {
    currentGameState.gameObjectState.updateGameObject(
      objectId,
      _.updatePermanentStatus(_.untap()))
  }
}
