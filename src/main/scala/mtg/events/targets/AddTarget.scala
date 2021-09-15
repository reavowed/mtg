package mtg.events.targets

import mtg.game.state.{InternalGameAction, GameActionResult, GameState}
import mtg.game.{ObjectId, ObjectOrPlayer}

case class AddTarget(stackObjectId: ObjectId, target: ObjectOrPlayer) extends InternalGameAction {
  override def execute(gameState: GameState): GameActionResult = {
    gameState.gameObjectState.updateStackObject(stackObjectId, _.addTarget(target))
  }
  override def canBeReverted: Boolean = true
}
