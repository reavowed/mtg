package mtg.stack.adding

import mtg.game.ObjectId
import mtg.game.state.history.LogEvent
import mtg.game.state.{ExecutableGameAction, GameActionResult, GameState, InternalGameAction, PartialGameActionResult}

case class FinishCasting(stackObjectId: ObjectId) extends ExecutableGameAction[Any] {
  override def execute()(implicit gameState: GameState): PartialGameActionResult[Any] = {
    val stackObjectWithState = gameState.gameObjectState.derivedState.stackObjectStates(stackObjectId)
    PartialGameActionResult.child(LogEvent.CastSpell(stackObjectWithState.controller, stackObjectWithState.characteristics.name.get, stackObjectWithState.gameObject.targets.map(_.getName(gameState))))
  }
}
