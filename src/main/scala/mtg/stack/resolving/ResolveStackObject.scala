package mtg.stack.resolving

import mtg.actions.moveZone.{MoveToBattlefieldAction, MoveToGraveyardAction}
import mtg.effects.StackObjectResolutionContext
import mtg.effects.targets.TargetIdentifier
import mtg.game.objects.{AbilityOnTheStack, StackObject}
import mtg.game.state.history.LogEvent
import mtg.game.state._
import mtg.stack.adding.TypeChecks

case class ResolveStackObject(stackObject: StackObject) extends ExecutableGameAction[Unit] {
  private def resolvePermanent(stackObjectWithState: StackObjectWithState)(implicit gameState: GameState): PartialGameActionResult[Unit] = {
    // RULE 608.3 / Apr 22 2021 : If the object that's resolving is a permanent spell, its resolution involves a single
    // step (unless it's an Aura, a copy of a permanent spell, or a mutating creature spell). The spell card becomes a
    // permanent and is put onto the battlefield under the control of the spell's controller.
    // TODO: Handle the exceptions above
    val controller = stackObjectWithState.controller
    PartialGameActionResult.childrenThenValue(
      Seq(
        WrappedOldUpdates(MoveToBattlefieldAction(stackObject.objectId, controller)),
        LogEvent.ResolvePermanent(controller, stackObjectWithState.characteristics.name.get)),
      ())(gameState)
  }

  private def shouldFizzleDueToInvalidTargets(stackObjectWithState: StackObjectWithState, gameState: GameState): Boolean = {
    val effectContext = stackObjectWithState.getEffectContext(gameState)
    stackObjectWithState.gameObject.targets.nonEmpty &&
      stackObject.targets.zip(TargetIdentifier.getAll(stackObjectWithState)).forall { case (target, identifier) =>
        !identifier.isValidTarget(stackObjectWithState, target, gameState, effectContext)
      }
  }

  private def resolveInstantOrSorcerySpell(spell: StackObjectWithState)(implicit gameState: GameState): PartialGameActionResult[Unit] = {
    val resolutionContext = StackObjectResolutionContext.forSpellOrAbility(spell, gameState)
    PartialGameActionResult.childrenThenValue(
      Seq(
        ResolveInstructions(spell.applicableEffectParagraphs.flatMap(_.instructions), resolutionContext),
        FinishResolvingInstantOrSorcerySpell(spell)),
      ())
  }

  private def resolveAbility(ability: StackObjectWithState)(implicit gameState: GameState): PartialGameActionResult[Unit] = {
    val resolutionContext = StackObjectResolutionContext.forSpellOrAbility(ability, gameState)
    PartialGameActionResult.childrenThenValue(
      Seq(
        ResolveInstructions(ability.applicableEffectParagraphs.flatMap(_.instructions), resolutionContext),
        FinishResolvingAbility(ability)),
      ())
  }

  override def execute()(implicit gameState: GameState): PartialGameActionResult[Unit] = {
    val stackObjectWithState = gameState.gameObjectState.derivedState.stackObjectStates(stackObject.objectId)
    if (TypeChecks.hasPermanentType(stackObjectWithState)) {
      resolvePermanent(stackObjectWithState)
    } else if (shouldFizzleDueToInvalidTargets(stackObjectWithState, gameState)) {
      PartialGameActionResult.childrenThenValue(
        Seq(
          WrappedOldUpdates(MoveToGraveyardAction(stackObject.objectId)),
          LogEvent.SpellFailedToResolve(stackObjectWithState.characteristics.name.get)),
        ())
    } else if (stackObject.underlyingObject.isInstanceOf[AbilityOnTheStack]) {
      resolveAbility(stackObjectWithState)
    } else {
      resolveInstantOrSorcerySpell(stackObjectWithState)
    }
  }
}
