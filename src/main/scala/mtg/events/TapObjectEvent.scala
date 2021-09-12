package mtg.events

import mtg.game.ObjectId
import mtg.game.state.{GameObjectEvent, GameObjectEventResult, GameState}

case class TapObjectEvent(objectId: ObjectId) extends GameObjectEvent {
  override def execute(currentGameState: GameState): GameObjectEventResult = {
    currentGameState.gameObjectState.updatePermanentObject(
      objectId,
      _.updatePermanentStatus(_.tap()))
  }
  override def canBeReverted: Boolean = true
}
