package mtg.stack.resolving

import mtg.actions.moveZone.{MoveToBattlefieldAction, MoveToGraveyardAction}
import mtg.effects.{EffectContext, StackObjectResolutionContext}
import mtg.game.objects.{AbilityOnTheStack, StackObject}
import mtg.game.state._
import mtg.game.state.history.LogEvent
import mtg.instructions.nounPhrases.Target
import mtg.stack.adding.TypeChecks

case class ResolveStackObject(stackObject: StackObject) extends DelegatingGameAction[Unit] {
  override def delegate(implicit gameState: GameState): GameAction[Unit] = {
    val stackObjectWithState = gameState.gameObjectState.derivedState.stackObjectStates(stackObject.objectId)
    if (TypeChecks.hasPermanentType(stackObjectWithState)) {
      resolvePermanent(stackObjectWithState)
    } else if (shouldFizzleDueToInvalidTargets(stackObjectWithState, gameState)) {
      MoveToGraveyardAction(stackObject.objectId)
        .andThen(LogEvent.SpellFailedToResolve(stackObjectWithState.characteristics.name.get))
    } else if (stackObject.underlyingObject.isInstanceOf[AbilityOnTheStack]) {
      resolveAbility(stackObjectWithState)
    } else {
      resolveInstantOrSorcerySpell(stackObjectWithState)
    }
  }

  private def resolvePermanent(stackObjectWithState: StackObjectWithState)(implicit gameState: GameState): GameAction[Unit] = {
    // RULE 608.3 / Apr 22 2021 : If the object that's resolving is a permanent spell, its resolution involves a single
    // step (unless it's an Aura, a copy of a permanent spell, or a mutating creature spell). The spell card becomes a
    // permanent and is put onto the battlefield under the control of the spell's controller.
    // TODO: Handle the exceptions above
    val controller = stackObjectWithState.controller
    MoveToBattlefieldAction(stackObject.objectId, controller)
      .andThen(LogEvent.ResolvePermanent(controller, stackObjectWithState.characteristics.name.get))
  }

  private def shouldFizzleDueToInvalidTargets(stackObjectWithState: StackObjectWithState, gameState: GameState): Boolean = {
    val effectContext = EffectContext(stackObjectWithState)
    stackObjectWithState.gameObject.targets.nonEmpty &&
      stackObject.targets.zip(Target.getAll(stackObjectWithState)).forall { case (target, identifier) =>
        !identifier.isValidTarget(stackObjectWithState, target, gameState, effectContext)
      }
  }

  private def resolveInstantOrSorcerySpell(spell: StackObjectWithState)(implicit gameState: GameState): GameAction[Unit] = {
    val resolutionContext = StackObjectResolutionContext.forSpellOrAbility(spell, gameState)
    ResolveInstructions(spell.applicableInstructionParagraphs.flatMap(_.instructions), resolutionContext)
      .andThen(FinishResolvingInstantOrSorcerySpell(spell))
  }

  private def resolveAbility(ability: StackObjectWithState)(implicit gameState: GameState): GameAction[Unit] = {
    val resolutionContext = StackObjectResolutionContext.forSpellOrAbility(ability, gameState)
    ResolveInstructions(ability.applicableInstructionParagraphs.flatMap(_.instructions), resolutionContext)
      .andThen(FinishResolvingAbility(ability))
  }
}
