package mtg.events

import mtg.game.state.{InternalGameAction, GameActionResult, GameState}
import mtg.game.{ObjectId, PlayerId, Zone}

case class DestroyEvent(player: PlayerId, objectId: ObjectId) extends InternalGameAction {
  override def execute(gameState: GameState): GameActionResult = {
    gameState.gameObjectState.allObjects.find(_.objectId == objectId).map(gameObject => {
      MoveObjectEvent(player, objectId, Zone.Graveyard(gameObject.owner))
    })
  }
  override def canBeReverted: Boolean = true
}
