package mtg.game.state.history

import mtg.game.turns.{Turn, TurnPhase, TurnStep}

case class StepHistory(turn: Turn, phase: TurnPhase, step: TurnStep, events: Seq[GameEvent]) {
  def addGameEvent(event: GameEvent): StepHistory = copy(events = events :+ event)
}
