package mtg.stack.resolving

import mtg.effects.StackObjectResolutionContext
import mtg.effects.targets.TargetIdentifier
import mtg.events.MoveObjectEvent
import mtg.game.Zone
import mtg.game.objects.{AbilityOnTheStack, StackObject}
import mtg.game.state.history.LogEvent
import mtg.game.state.{GameActionResult, GameState, InternalGameAction, StackObjectWithState}

case class ResolveStackObject(stackObject: StackObject) extends InternalGameAction {
  private def resolvePermanent(stackObjectWithState: StackObjectWithState): GameActionResult = {
    // RULE 608.3 / Apr 22 2021 : If the object that's resolving is a permanent spell, its resolution involves a single
    // step (unless it's an Aura, a copy of a permanent spell, or a mutating creature spell). The spell card becomes a
    // permanent and is put onto the battlefield under the control of the spell's controller.
    // TODO: Handle the exceptions above
    val controller = stackObjectWithState.controller
    (
      MoveObjectEvent(controller, stackObject, Zone.Battlefield),
      LogEvent.ResolvePermanent(controller, stackObjectWithState.characteristics.name.get)
    )
  }

  private def shouldFizzleDueToInvalidTargets(stackObjectWithState: StackObjectWithState, gameState: GameState): Boolean = {
    val effectContext = stackObjectWithState.getEffectContext(gameState)
    stackObjectWithState.gameObject.targets.nonEmpty &&
      stackObject.targets.zip(TargetIdentifier.getAll(stackObjectWithState)).forall { case (target, identifier) =>
        !identifier.isValidTarget(stackObjectWithState, target, gameState, effectContext)
      }
  }

  private def resolveInstantOrSorcerySpell(spell: StackObjectWithState, gameState: GameState): GameActionResult = {
    val resolutionContext = StackObjectResolutionContext.forSpellOrAbility(spell, gameState)
    Seq(
      ResolveEffects(spell.applicableEffectParagraphs.flatMap(_.effects), resolutionContext),
      FinishResolvingInstantOrSorcerySpell(spell)
    )
  }

  private def resolveAbility(ability: StackObjectWithState, gameState: GameState): GameActionResult = {
    val resolutionContext = StackObjectResolutionContext.forSpellOrAbility(ability, gameState)
    Seq(
      ResolveEffects(ability.applicableEffectParagraphs.flatMap(_.effects), resolutionContext),
      FinishResolvingAbility(ability)
    )
  }

  override def execute(gameState: GameState): GameActionResult = {
    val stackObjectWithState = stackObject.currentState(gameState)
    if (stackObjectWithState.characteristics.types.exists(_.isPermanent)) {
      resolvePermanent(stackObjectWithState)
    } else if (shouldFizzleDueToInvalidTargets(stackObjectWithState, gameState)) {
      (
        MoveObjectEvent(stackObjectWithState.controller, stackObject, Zone.Graveyard(stackObjectWithState.gameObject.owner)),
        LogEvent.SpellFailedToResolve(stackObjectWithState.characteristics.name.get)
      )
    } else if (stackObject.underlyingObject.isInstanceOf[AbilityOnTheStack]) {
      resolveAbility(stackObjectWithState, gameState)
    } else {
      resolveInstantOrSorcerySpell(stackObjectWithState, gameState)
    }
  }

  override def canBeReverted: Boolean = false
}
