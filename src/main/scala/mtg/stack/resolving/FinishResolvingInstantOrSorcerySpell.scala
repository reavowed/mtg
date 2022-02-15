package mtg.stack.resolving

import mtg.events.moveZone.MoveToGraveyardEvent
import mtg.game.state.history.LogEvent
import mtg.game.state.{GameActionResult, GameState, InternalGameAction, StackObjectWithState}

case class FinishResolvingInstantOrSorcerySpell(spell: StackObjectWithState) extends InternalGameAction {
  override def execute(gameState: GameState): GameActionResult = {
    (MoveToGraveyardEvent(spell.gameObject.objectId), LogEvent.ResolveSpell(spell.controller, spell.characteristics.name.get))
  }

  override def canBeReverted: Boolean = false
}
