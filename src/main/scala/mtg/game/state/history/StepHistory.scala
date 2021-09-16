package mtg.game.state.history

import mtg.game.turns.{Turn, TurnPhase, TurnStep}

case class StepHistory(turn: Turn, phase: TurnPhase, step: TurnStep, gameEvents: Seq[HistoryEvent]) {
  def addGameEvent(event: HistoryEvent): StepHistory = copy(gameEvents = gameEvents :+ event)
}
