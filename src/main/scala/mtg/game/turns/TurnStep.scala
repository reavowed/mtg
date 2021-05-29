package mtg.game.turns

import mtg.game.state.GameAction
import mtg.game.turns.priority.PriorityFromActivePlayerAction
import mtg.game.turns.turnBasedActions._
import mtg.utils.CaseObject

sealed abstract class TurnStep(val actions: Seq[GameAction]) extends CaseObject

object TurnStep {
  case object UntapStep extends TurnStep(Seq(UntapForTurn))
  case object UpkeepStep extends TurnStep(Seq(PriorityFromActivePlayerAction))
  case object DrawStep extends TurnStep(Seq(DrawForTurn, PriorityFromActivePlayerAction))

  val BeginningPhaseSteps = Seq(UntapStep, UpkeepStep, DrawStep)

  case object BeginningOfCombatStep extends TurnStep(Seq(PriorityFromActivePlayerAction))
  case object DeclareAttackersStep extends TurnStep(Seq(DeclareAttackers, PriorityFromActivePlayerAction))
  case object DeclareBlockersStep extends TurnStep(Seq(DeclareBlockers, PriorityFromActivePlayerAction))
  case object CombatDamageStep extends TurnStep(Seq(CombatDamage, PriorityFromActivePlayerAction))
  case object EndOfCombatStep extends TurnStep(Seq(PriorityFromActivePlayerAction))

  val CombatPhaseSteps = Seq(BeginningOfCombatStep, DeclareAttackersStep, DeclareBlockersStep, CombatDamageStep, EndOfCombatStep)

  case object EndStep extends TurnStep(Seq(PriorityFromActivePlayerAction))
  case object CleanupStep extends TurnStep(Seq(CleanupAction))

  val EndingPhaseSteps = Seq(EndStep, CleanupStep)
}
