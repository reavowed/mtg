package mtg.game.turns

import mtg.game.priority.PrioritySequenceAction
import mtg.game.state.{GameAction, WrappedOldUpdates}
import mtg.game.turns.turnBasedActions._
import mtg.utils.CaseObjectWithName

sealed abstract class TurnStep(val actions: Seq[GameAction[Any]]) extends CaseObjectWithName

object TurnStep {
  case object UntapStep extends TurnStep(Seq(WrappedOldUpdates(UntapForTurn)))
  case object UpkeepStep extends TurnStep(Seq(PrioritySequenceAction))
  case object DrawStep extends TurnStep(Seq(WrappedOldUpdates(DrawForTurn), PrioritySequenceAction))

  val BeginningPhaseSteps = Seq(UntapStep, UpkeepStep, DrawStep)

  case object BeginningOfCombatStep extends TurnStep(Seq(PrioritySequenceAction))
  case object DeclareAttackersStep extends TurnStep(Seq(WrappedOldUpdates(DeclareAttackers), PrioritySequenceAction))
  case object DeclareBlockersStep extends TurnStep(Seq(WrappedOldUpdates(DeclareBlockers), PrioritySequenceAction))
  case object CombatDamageStep extends TurnStep(Seq(WrappedOldUpdates(CombatDamage), PrioritySequenceAction))
  case object EndOfCombatStep extends TurnStep(Seq(PrioritySequenceAction))

  val CombatPhaseSteps = Seq(BeginningOfCombatStep, DeclareAttackersStep, DeclareBlockersStep, CombatDamageStep, EndOfCombatStep)

  case object EndStep extends TurnStep(Seq(PrioritySequenceAction))
  case object CleanupStep extends TurnStep(Seq(WrappedOldUpdates(CleanupAction)))

  val EndingPhaseSteps = Seq(EndStep, CleanupStep)
}
