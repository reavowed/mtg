package mtg.game.stack.steps

import mtg.game.ObjectId
import mtg.game.actions.cast
import mtg.game.state.history.LogEvent
import mtg.game.state.{GameState, InternalGameAction, InternalGameActionResult}
import mtg.game.turns.priority

case class FinishCasting(stackObjectId: ObjectId) extends InternalGameAction {
  override def execute(currentGameState: GameState): InternalGameActionResult = {
    currentGameState.gameObjectState.derivedState.spellStates.get(stackObjectId).map[InternalGameActionResult] { stackObjectWithState =>
      (
        Seq(cast.SpellCastEvent(stackObjectId), priority.PriorityFromPlayerAction(stackObjectWithState.controller)),
        LogEvent.CastSpell(stackObjectWithState.controller, stackObjectWithState.characteristics.name.get, stackObjectWithState.gameObject.targets.map(_.getName(currentGameState)))
      )
    }.getOrElse(())
  }
  override def canBeReverted: Boolean = true
}
