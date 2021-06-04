package mtg.game.actions

import mtg.abilities.SpellAbility
import mtg.effects.StackObjectResolutionContext
import mtg.events.MoveObjectEvent
import mtg.game.Zone
import mtg.game.state.history.LogEvent
import mtg.game.state.{GameActionResult, GameState, InternalGameAction, StackObjectWithState}

case class ResolveInstantOrSorcerySpell(spell: StackObjectWithState) extends InternalGameAction {
  override def execute(currentGameState: GameState): GameActionResult = {
    val resolutionContext = StackObjectResolutionContext.initial(spell)
    Seq(
      ResolveEffects(spell.characteristics.abilities.ofType[SpellAbility].flatMap(_.effects), resolutionContext),
      FinishResolvingInstantOrSorcerySpell(spell)
    )
  }
}

case class FinishResolvingInstantOrSorcerySpell(spell: StackObjectWithState) extends InternalGameAction {
  override def execute(currentGameState: GameState): GameActionResult = {
    (MoveObjectEvent(spell.controller, spell.gameObject, Zone.Graveyard(spell.gameObject.owner)), LogEvent.ResolveSpell(spell.controller, spell.characteristics.name))
  }
}
