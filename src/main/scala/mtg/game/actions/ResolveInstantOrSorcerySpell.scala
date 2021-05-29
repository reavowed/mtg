package mtg.game.actions

import mtg.abilities.SpellAbility
import mtg.effects.ResolutionContext
import mtg.events.MoveObjectEvent
import mtg.game.Zone
import mtg.game.state.{GameState, InternalGameAction, InternalGameActionResult, ObjectWithState}

case class ResolveInstantOrSorcerySpell(spell: ObjectWithState) extends InternalGameAction {
  override def execute(currentGameState: GameState): InternalGameActionResult = {
    val resolutionContext = ResolutionContext.initial(spell)
    Seq(
      ResolveEffects(spell.characteristics.abilities.ofType[SpellAbility].flatMap(_.effects), resolutionContext),
      MoveObjectEvent(resolutionContext.controller, spell.gameObject, Zone.Graveyard(resolutionContext.controller))
    )
  }
}
