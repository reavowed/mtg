package mtg.game.turns

import mtg.game.state.GameAction

sealed abstract class TurnStep(val actions: Seq[GameAction])

object TurnStep {
  case object UntapStep extends TurnStep(Nil)
  case object UpkeepStep extends TurnStep(Seq(PriorityAction))
  case object DrawStep extends TurnStep(Nil)

  val BeginningPhaseSteps = Seq(UntapStep, UpkeepStep, DrawStep)

  case object BeginningOfCombatStep extends TurnStep(Nil)
  case object DeclareAttackersStep extends TurnStep(Nil)
  case object DeclareBlockersStep extends TurnStep(Nil)
  case object CombatDamageStep extends TurnStep(Nil)
  case object EndOfCombatStep extends TurnStep(Nil)

  val CombatPhaseSteps = Seq(BeginningOfCombatStep, DeclareAttackersStep, DeclareBlockersStep, CombatDamageStep, EndOfCombatStep)

  case object EndStep extends TurnStep(Nil)
  case object CleanupStep extends TurnStep(Nil)

  val EndingPhaseSteps = Seq(EndStep, CleanupStep)
}