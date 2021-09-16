package mtg.game.state.history

import mtg.game.turns.{Turn, TurnPhase, TurnStep}

case class TurnHistory(turn: Turn, phases: Seq[PhaseHistory]) {
  def startPhase(phase: TurnPhase): TurnHistory = {
    copy(phases = phases :+ PhaseHistory(turn, phase))
  }
  def startStep(step: TurnStep): TurnHistory = {
    copy(phases = phases.init :+ phases.last.startStep(step))
  }
  def addGameEvent(event: HistoryEvent): TurnHistory = {
    copy(phases = phases.init :+ phases.last.addGameEvent(event))
  }
  def gameEvents: Seq[HistoryEvent] = {
    phases.flatMap(_.gameEvents)
  }
}
object TurnHistory {
  def forTurn(turn: Turn): TurnHistory = TurnHistory(turn, Nil)
}
