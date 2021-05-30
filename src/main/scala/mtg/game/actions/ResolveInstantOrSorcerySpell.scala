package mtg.game.actions

import mtg.abilities.SpellAbility
import mtg.effects.ResolutionContext
import mtg.events.MoveObjectEvent
import mtg.game.Zone
import mtg.game.state.history.LogEvent
import mtg.game.state.{GameState, InternalGameAction, InternalGameActionResult, StackObjectWithState}

case class ResolveInstantOrSorcerySpell(spell: StackObjectWithState) extends InternalGameAction {
  override def execute(currentGameState: GameState): InternalGameActionResult = {
    val resolutionContext = ResolutionContext.initial(spell)
    Seq(
      ResolveEffects(spell.characteristics.abilities.ofType[SpellAbility].flatMap(_.effects), resolutionContext),
      FinishResolvingInstantOrSorcerySpell(spell)
    )
  }
}

case class FinishResolvingInstantOrSorcerySpell(spell: StackObjectWithState) extends InternalGameAction {
  override def execute(currentGameState: GameState): InternalGameActionResult = {
    (MoveObjectEvent(spell.controller, spell.gameObject, Zone.Graveyard(spell.controller)), LogEvent.ResolveSpell(spell.controller, spell.characteristics.name))
  }
}
