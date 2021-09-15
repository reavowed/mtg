package mtg.game.state.history

import mtg._
import mtg.game.state.{DerivedState, InternalGameAction}

import scala.reflect.{ClassTag, classTag}

sealed trait GameEvent
object GameEvent {
  case class ResolvedAction(action: InternalGameAction, stateBefore: DerivedState) extends GameEvent

  implicit class GameEventSeqOps(seq: Iterable[GameEvent]) {
    def since[T <: InternalGameAction : ClassTag]: Iterable[GameEvent] = seq.takeWhile {
      case ResolvedAction(e, _) if classTag[T].runtimeClass.isInstance(e) => false
      case _ => true
    }
    def actions: Iterable[InternalGameAction] = seq.ofType[ResolvedAction].map(_.action)
  }
}
