package mtg.game.state.history

import mtg._
import mtg.game.state.{Choice, GameAction, GameState}

import scala.reflect.ClassTag

sealed trait HistoryEvent {
  def stateBefore: GameState
}
object HistoryEvent {
  case class ResolvedAction[T](action: GameAction[T], result: T, stateBefore: GameState) extends HistoryEvent
  case class ResolvedChoice[DecisionType](choice: Choice[DecisionType], decision: DecisionType, stateBefore: GameState) extends HistoryEvent

  implicit class GameEventSeqOps(seq: Iterable[HistoryEvent]) {
    def actions: Iterable[GameAction[_]] = seq.ofType[ResolvedAction[_]].map(_.action)
    def decisions[DecisionType : ClassTag]: Iterable[DecisionType] = seq.ofType[ResolvedChoice[_]].map(_.decision).ofType[DecisionType]
  }
}
