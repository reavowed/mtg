package mtg.stack.resolving

import mtg.actions.moveZone.MoveToGraveyardAction
import mtg.game.state.history.LogEvent
import mtg.game.state.{ExecutableGameAction, GameActionResult, GameState, InternalGameAction, PartialGameActionResult, StackObjectWithState, WrappedOldUpdates}

case class FinishResolvingInstantOrSorcerySpell(spell: StackObjectWithState) extends ExecutableGameAction[Unit] {
  override def execute()(implicit gameState: GameState): PartialGameActionResult[Unit] = {
    PartialGameActionResult.childrenThenValue(
      Seq(
        WrappedOldUpdates(MoveToGraveyardAction(spell.gameObject.objectId)),
        LogEvent.ResolveSpell(spell.controller, spell.characteristics.name.get)),
      ())
  }
}
