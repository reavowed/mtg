package mtg.game.turns

import mtg.game.state.history.GameEvent.ResolvedEvent
import mtg.game.state.{GameAction, GameState}
import mtg.game.turns.priority.PriorityFromActivePlayerAction
import mtg.game.turns.turnEvents.{BeginStepAction, EndPhaseAction}
import mtg.utils.CaseObjectWithName

sealed abstract class TurnPhase(val actions: Seq[GameAction]) extends CaseObjectWithName {
  def hasFinished(gameState: GameState): Boolean = {
    gameState.eventsThisTurn.ofType[ResolvedEvent].exists(_.event == EndPhaseAction(this))
  }
}

sealed abstract class TurnPhaseWithSteps(val steps: Seq[TurnStep]) extends TurnPhase(steps.map(BeginStepAction))
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
