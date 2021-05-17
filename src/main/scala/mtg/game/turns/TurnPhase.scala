package mtg.game.turns

import mtg.game.state.GameAction

sealed abstract class TurnPhase(val actions: Seq[GameAction])

sealed abstract class TurnPhaseWithSteps(val steps: Seq[TurnStep]) extends TurnPhase(steps.map(BeginStepEvent))
sealed abstract class TurnPhaseWithoutSteps extends TurnPhase(Seq(PriorityAction))

object TurnPhase {
  case object BeginningPhase extends TurnPhaseWithSteps(TurnStep.BeginningPhaseSteps)
  case object PrecombatMainPhase extends TurnPhaseWithoutSteps
  case object CombatPhase extends TurnPhaseWithSteps(TurnStep.CombatPhaseSteps)
  case object PostcombatMainPhase extends TurnPhaseWithoutSteps
  case object EndingPhase extends TurnPhaseWithSteps(TurnStep.EndingPhaseSteps)

  val All = Seq(BeginningPhase, PrecombatMainPhase, CombatPhase, PostcombatMainPhase, EndingPhase)
}
