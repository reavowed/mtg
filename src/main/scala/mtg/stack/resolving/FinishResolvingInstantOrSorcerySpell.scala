package mtg.stack.resolving

import mtg.actions.moveZone.MoveToGraveyardAction
import mtg.game.state.history.LogEvent
import mtg.game.state.{GameActionResult, GameState, InternalGameAction, StackObjectWithState}

case class FinishResolvingInstantOrSorcerySpell(spell: StackObjectWithState) extends InternalGameAction {
  override def execute(gameState: GameState): GameActionResult = {
    (MoveToGraveyardAction(spell.gameObject.objectId), LogEvent.ResolveSpell(spell.controller, spell.characteristics.name.get))
  }

  override def canBeReverted: Boolean = false
}
