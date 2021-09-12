package mtg.game.stack

import mtg.events.MoveObjectEvent
import mtg.game.Zone
import mtg.game.state.history.LogEvent
import mtg.game.state.{InternalGameActionResult, GameState, InternalGameAction, StackObjectWithState}

case class FinishResolvingInstantOrSorcerySpell(spell: StackObjectWithState) extends InternalGameAction {
  override def execute(currentGameState: GameState): InternalGameActionResult = {
    (MoveObjectEvent(spell.controller, spell.gameObject, Zone.Graveyard(spell.gameObject.owner)), LogEvent.ResolveSpell(spell.controller, spell.characteristics.name.get))
  }
  override def canBeReverted: Boolean = false
}
