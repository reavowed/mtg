package mtg.game.state.history

import mtg.game.turns.{Turn, TurnPhase, TurnPhaseWithSteps, TurnPhaseWithoutSteps, TurnStep}

sealed abstract class PhaseHistory {
  def turn: Turn
  def phase: TurnPhase
  def startStep(step: TurnStep): PhaseHistory
  def addGameEvent(event: GameEvent): PhaseHistory
}
object PhaseHistory {
  def apply(turn: Turn, phase: TurnPhase): PhaseHistory = {
    phase match {
      case _: TurnPhaseWithSteps =>
        PhaseHistoryWithSteps(turn, phase, Nil)
      case _: TurnPhaseWithoutSteps =>
        PhaseHistoryWithoutSteps(turn, phase, Nil)
    }
  }
}

case class PhaseHistoryWithSteps(turn: Turn, phase: TurnPhase, steps: Seq[StepHistory]) extends PhaseHistory {
  override def startStep(step: TurnStep): PhaseHistory = {
    copy(steps = steps :+ StepHistory(turn, phase, step, Nil))
  }
  def addGameEvent(event: GameEvent): PhaseHistory = {
    copy(steps = steps.init :+ steps.last.addGameEvent(event))
  }
}
case class PhaseHistoryWithoutSteps(turn: Turn, phase: TurnPhase, events: Seq[GameEvent]) extends PhaseHistory {
  override def startStep(step: TurnStep): PhaseHistory = throw new RuntimeException("Cannot add step to " + phase.getClass.getSimpleName)
  def addGameEvent(event: GameEvent): PhaseHistory = {
    copy(events = events :+ event)
  }
}
