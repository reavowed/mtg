package mtg.game.state.history

import mtg.game.turns._

sealed abstract class PhaseHistory {
  def turn: Turn
  def phase: TurnPhase
  def startStep(step: TurnStep): PhaseHistory
  def addGameEvent(event: GameEvent): PhaseHistory
  def gameEvents: Seq[GameEvent]
}
object PhaseHistory {
  def apply(turn: Turn, phase: TurnPhase): PhaseHistory = {
    phase match {
      case _: TurnPhaseWithSteps =>
        PhaseHistoryWithSteps(turn, phase, Nil)
      case _: MainPhase =>
        PhaseHistoryWithoutSteps(turn, phase, Nil)
    }
  }
}

case class PhaseHistoryWithSteps(turn: Turn, phase: TurnPhase, steps: Seq[StepHistory]) extends PhaseHistory {
  override def startStep(step: TurnStep): PhaseHistory = {
    copy(steps = steps :+ StepHistory(turn, phase, step, Nil))
  }
  override def addGameEvent(event: GameEvent): PhaseHistory = {
    copy(steps = steps.init :+ steps.last.addGameEvent(event))
  }
  override def gameEvents: Seq[GameEvent] = steps.flatMap(_.gameEvents)
}
case class PhaseHistoryWithoutSteps(turn: Turn, phase: TurnPhase, gameEvents: Seq[GameEvent]) extends PhaseHistory {
  override def startStep(step: TurnStep): PhaseHistory = throw new RuntimeException("Cannot add step to " + phase.getClass.getSimpleName)
  override def addGameEvent(event: GameEvent): PhaseHistory = {
    copy(gameEvents = gameEvents :+ event)
  }
}
