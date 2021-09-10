package mtg.game.stack

import mtg.abilities.SpellAbility
import mtg.effects.StackObjectResolutionContext
import mtg.effects.targets.TargetIdentifier
import mtg.events.MoveObjectAction
import mtg.game.Zone
import mtg.game.objects.{AbilityOnTheStack, StackObject}
import mtg.game.state.history.LogEvent
import mtg.game.state.{InternalGameActionResult, GameState, InternalGameAction, StackObjectWithState}

case class ResolveStackObject(stackObject: StackObject) extends InternalGameAction {
  private def resolvePermanent(stackObjectWithState: StackObjectWithState): InternalGameActionResult = {
    // RULE 608.3 / Apr 22 2021 : If the object that's resolving is a permanent spell, its resolution involves a single
    // step (unless it's an Aura, a copy of a permanent spell, or a mutating creature spell). The spell card becomes a
    // permanent and is put onto the battlefield under the control of the spell's controller.
    // TODO: Handle the exceptions above
    val controller = stackObjectWithState.controller
    (
      MoveObjectAction(controller, stackObject, Zone.Battlefield),
      LogEvent.ResolvePermanent(controller, stackObjectWithState.characteristics.name.get)
    )
  }

  private def shouldFizzleDueToInvalidTargets(stackObjectWithState: StackObjectWithState, currentGameState: GameState): Boolean = {
    val effectContext = stackObjectWithState.getEffectContext(currentGameState)
    stackObjectWithState.gameObject.targets.nonEmpty &&
      stackObject.targets.zip(TargetIdentifier.getAll(stackObjectWithState)).forall { case (target, identifier) =>
        !identifier.isValidTarget(stackObjectWithState, target, currentGameState, effectContext)
      }
  }

  private def resolveInstantOrSorcerySpell(spell: StackObjectWithState, currentGameState: GameState): InternalGameActionResult = {
    val resolutionContext = StackObjectResolutionContext.forSpellOrAbility(spell, currentGameState)
    Seq(
      ResolveEffects(spell.characteristics.abilities.ofType[SpellAbility].flatMap(_.effects), resolutionContext),
      FinishResolvingInstantOrSorcerySpell(spell)
    )
  }

  private def resolveAbility(ability: StackObjectWithState, currentGameState: GameState): InternalGameActionResult = {
    val resolutionContext = StackObjectResolutionContext.forSpellOrAbility(ability, currentGameState)
    Seq(
      ResolveEffects(ability.characteristics.abilities.ofType[SpellAbility].flatMap(_.effects), resolutionContext),
      FinishResolvingAbility(ability)
    )
  }

  override def execute(currentGameState: GameState): InternalGameActionResult = {
    val stackObjectWithState = stackObject.currentState(currentGameState)
    if (stackObjectWithState.characteristics.types.exists(_.isPermanent)) {
      resolvePermanent(stackObjectWithState)
    } else if (shouldFizzleDueToInvalidTargets(stackObjectWithState, currentGameState)) {
      (
        MoveObjectAction(stackObjectWithState.controller, stackObject, Zone.Graveyard(stackObjectWithState.gameObject.owner)),
        LogEvent.SpellFailedToResolve(stackObjectWithState.characteristics.name.get)
      )
    } else if (stackObject.underlyingObject.isInstanceOf[AbilityOnTheStack]) {
      resolveAbility(stackObjectWithState, currentGameState)
    } else {
      resolveInstantOrSorcerySpell(stackObjectWithState, currentGameState)
    }
  }
}
