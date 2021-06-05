package mtg.game.turns.priority

import mtg.effects.AbilityContext
import mtg.effects.targets.TargetIdentifier
import mtg.events.MoveObjectEvent
import mtg.game.Zone
import mtg.game.actions.ResolveInstantOrSorcerySpell
import mtg.game.objects.StackObject
import mtg.game.state.history.LogEvent
import mtg.game.state.{GameActionResult, GameState, InternalGameAction}

case class ResolveStackObject(stackObject: StackObject) extends InternalGameAction {
  override def execute(currentGameState: GameState): GameActionResult = {
    val stackObjectWithState = stackObject.currentState(currentGameState)
    if (stackObjectWithState.characteristics.types.exists(_.isPermanent)) {
      // RULE 608.3 / Apr 22 2021 : If the object that's resolving is a permanent spell, its resolution involves a single
      // step (unless it's an Aura, a copy of a permanent spell, or a mutating creature spell). The spell card becomes a
      // permanent and is put onto the battlefield under the control of the spell's controller.
      // TODO: Handle the exceptions above
      val controller = stackObjectWithState.controller
      GameActionResult(
        Seq(MoveObjectEvent(controller, stackObject, Zone.Battlefield)),
        Some(LogEvent.ResolvePermanent(controller, stackObjectWithState.characteristics.name))
      )
    } else {
      def hasTargets = stackObject.targets.nonEmpty
      def areAllTargetsInvalid = stackObject.targets.zip(TargetIdentifier.getAll(stackObjectWithState)).forall { case (target, identifier) =>
        !identifier.isValidTarget(stackObjectWithState, target, currentGameState, AbilityContext(stackObjectWithState.controller))
      }
      if (hasTargets && areAllTargetsInvalid) {
        (
          MoveObjectEvent(stackObjectWithState.controller, stackObject, Zone.Graveyard(stackObjectWithState.gameObject.owner)),
          LogEvent.SpellFailedToResolve(stackObjectWithState.characteristics.name)
        )
      } else {
        ResolveInstantOrSorcerySpell(stackObjectWithState)
      }
    }
  }
}
