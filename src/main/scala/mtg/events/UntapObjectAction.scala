package mtg.events

import mtg.game.ObjectId
import mtg.game.state.{GameObjectAction, GameObjectActionResult, GameState}

case class UntapObjectAction(objectId: ObjectId) extends GameObjectAction {
  override def execute(currentGameState: GameState): GameObjectActionResult = {
    currentGameState.gameObjectState.updatePermanentObject(
      objectId,
      _.updatePermanentStatus(_.untap()))
  }
}
