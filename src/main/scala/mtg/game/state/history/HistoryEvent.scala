package mtg.game.state.history

import mtg._
import mtg.game.state.{GameState, InternalGameAction, Choice}

import scala.reflect.{ClassTag, classTag}

sealed trait HistoryEvent {
  def stateBefore: GameState
}
object HistoryEvent {
  case class ResolvedAction(action: InternalGameAction, stateBefore: GameState) extends HistoryEvent
  case class ResolvedChoice[DecisionType](choice: Choice[DecisionType], decision: DecisionType, stateBefore: GameState) extends HistoryEvent

  implicit class GameEventSeqOps(seq: Iterable[HistoryEvent]) {
    def since[T <: InternalGameAction : ClassTag]: Iterable[HistoryEvent] = seq.takeWhile {
      case ResolvedAction(e, _) if classTag[T].runtimeClass.isInstance(e) => false
      case _ => true
    }
    def actions: Iterable[InternalGameAction] = seq.ofType[ResolvedAction].map(_.action)
    def decisions[DecisionType : ClassTag]: Iterable[DecisionType] = seq.ofType[ResolvedChoice[_]].map(_.decision).ofType[DecisionType]
  }
}
