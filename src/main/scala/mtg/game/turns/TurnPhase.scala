package mtg.game.turns

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import mtg.game.state.GameAction
import mtg.game.turns.priority.PriorityFromActivePlayerAction
import mtg.game.turns.turnEvents.BeginStepEvent
import mtg.utils.CaseObjectSerializer

@JsonSerialize(using = classOf[CaseObjectSerializer])
sealed abstract class TurnPhase(val actions: Seq[GameAction])

sealed abstract class TurnPhaseWithSteps(val steps: Seq[TurnStep]) extends TurnPhase(steps.map(BeginStepEvent))
sealed abstract class MainPhase extends TurnPhase(Seq(PriorityFromActivePlayerAction))

object TurnPhase {
  case object BeginningPhase extends TurnPhaseWithSteps(TurnStep.BeginningPhaseSteps)
  case object PrecombatMainPhase extends MainPhase
  case object CombatPhase extends TurnPhaseWithSteps(TurnStep.CombatPhaseSteps)
  case object PostcombatMainPhase extends MainPhase
  case object EndingPhase extends TurnPhaseWithSteps(TurnStep.EndingPhaseSteps)

  val All = Seq(BeginningPhase, PrecombatMainPhase, CombatPhase, PostcombatMainPhase, EndingPhase)
  val AllPhasesAndSteps = All ++ All.ofType[TurnPhaseWithSteps].flatMap(_.steps)
}
