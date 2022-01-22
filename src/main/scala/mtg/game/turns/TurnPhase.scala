package mtg.game.turns

import mtg.game.priority.PrioritySequenceAction
import mtg.game.state.GameAction
import mtg.game.turns.turnEvents.ExecuteStep
import mtg.utils.CaseObjectWithName

sealed abstract class TurnPhase(val actions: Seq[GameAction[Any]]) extends CaseObjectWithName

sealed abstract class TurnPhaseWithSteps(val steps: Seq[TurnStep]) extends TurnPhase(steps.map(ExecuteStep))
sealed abstract class MainPhase extends TurnPhase(Seq(PrioritySequenceAction))

object TurnPhase {
  case object BeginningPhase extends TurnPhaseWithSteps(TurnStep.BeginningPhaseSteps)
  case object PrecombatMainPhase extends MainPhase
  case object CombatPhase extends TurnPhaseWithSteps(TurnStep.CombatPhaseSteps)
  case object PostcombatMainPhase extends MainPhase
  case object EndingPhase extends TurnPhaseWithSteps(TurnStep.EndingPhaseSteps)

  val All = Seq(BeginningPhase, PrecombatMainPhase, CombatPhase, PostcombatMainPhase, EndingPhase)
  val AllPhasesAndSteps = All ++ All.ofType[TurnPhaseWithSteps].flatMap(_.steps)
}
