package mtg.game.actions.spellsAndAbilities

import mtg.game.ObjectId
import mtg.game.actions.cast
import mtg.game.state.history.LogEvent
import mtg.game.state.{GameActionResult, GameState, InternalGameAction}
import mtg.game.turns.priority

case class FinishCasting(stackObjectId: ObjectId) extends InternalGameAction {
  override def execute(currentGameState: GameState): GameActionResult = {
    currentGameState.gameObjectState.derivedState.spellStates.get(stackObjectId).map[GameActionResult] { stackObjectWithState =>
      (
        Seq(cast.SpellCastEvent(stackObjectId), priority.PriorityFromPlayerAction(stackObjectWithState.controller)),
        LogEvent.CastSpell(stackObjectWithState.controller, stackObjectWithState.characteristics.name, stackObjectWithState.gameObject.targets.map(_.getName(currentGameState)))
      )
    }.getOrElse(())
  }
}
