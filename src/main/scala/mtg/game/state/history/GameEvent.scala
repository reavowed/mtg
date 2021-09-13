package mtg.game.state.history

import mtg._
import mtg.game.PlayerId
import mtg.game.state.{AutomaticGameAction, DerivedState}

import scala.reflect.{ClassTag, classTag}

sealed trait GameEvent
object GameEvent {
  case class Decision(chosenOption: AnyRef, playerIdentifier: PlayerId) extends GameEvent
  case class ResolvedAction(action: AutomaticGameAction, stateBefore: DerivedState) extends GameEvent

  implicit class GameEventSeqOps(seq: Iterable[GameEvent]) {
    def since[T <: AutomaticGameAction : ClassTag]: Iterable[GameEvent] = seq.takeWhile {
      case ResolvedAction(e, _) if classTag[T].runtimeClass.isInstance(e) => false
      case _ => true
    }
    def actions: Iterable[AutomaticGameAction] = seq.ofType[ResolvedAction].map(_.action)
    def getDecisions[T : ClassTag]: Iterable[T] = {
      seq.view.ofType[Decision]
        .map(_.chosenOption)
        .mapCollect(_.asOptionalInstanceOf[T])
    }
    def getDecision[T : ClassTag]: Option[T] = {
      seq.view.ofType[Decision]
        .map(_.chosenOption)
        .mapFind(_.asOptionalInstanceOf[T])
    }
  }
}
