package mtg.stack.adding

import mtg.game.ObjectId
import mtg.game.actions.cast
import mtg.game.state.history.LogEvent
import mtg.game.state.{GameActionResult, GameState, InternalGameAction}

case class FinishCasting(stackObjectId: ObjectId) extends InternalGameAction {
  override def execute(gameState: GameState): GameActionResult = {
    gameState.gameObjectState.derivedState.stackObjectStates.get(stackObjectId).map[GameActionResult] { stackObjectWithState =>
      (
        Seq(cast.SpellCastEvent(stackObjectId)),
        LogEvent.CastSpell(stackObjectWithState.controller, stackObjectWithState.characteristics.name.get, stackObjectWithState.gameObject.targets.map(_.getName(gameState)))
      )
    }.getOrElse(())
  }

  override def canBeReverted: Boolean = true
}
