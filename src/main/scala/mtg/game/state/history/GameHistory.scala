package mtg.game.state.history

import mtg.game.turns.{Turn, TurnPhase, TurnStep}

case class GameHistory(preGameEvents: Seq[GameEvent], turns: Seq[TurnHistory], logEvents: Seq[TimestampedLogEvent]) {
  def startTurn(turn: Turn): GameHistory = {
    copy(turns = turns :+ TurnHistory.forTurn(turn))
  }
  def startPhase(phase: TurnPhase): GameHistory = {
    copy(turns = turns.init :+ turns.last.startPhase(phase))
  }
  def startStep(step: TurnStep): GameHistory = {
    copy(turns = turns.init :+ turns.last.startStep(step))
  }

  def addGameEvent(event: GameEvent): GameHistory = turns match {
    case init :+ last =>
      copy(turns = init :+ last.addGameEvent(event))
    case Nil =>
      copy(preGameEvents = preGameEvents :+ event)
  }
  def addLogEvent(event: LogEvent): GameHistory = {
    copy(logEvents = logEvents :+ TimestampedLogEvent(event))
  }
}
object GameHistory {
  val empty = GameHistory(Nil, Nil, Nil)
}


