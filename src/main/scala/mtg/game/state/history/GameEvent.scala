package mtg.game.state.history

import mtg._
import mtg.game.PlayerId
import mtg.game.state.{DerivedState, GameObjectEvent}

import scala.reflect.{ClassTag, classTag}

sealed trait GameEvent
object GameEvent {
  case class Decision(chosenOption: AnyRef, playerIdentifier: PlayerId) extends GameEvent
  case class ResolvedEvent(event: GameObjectEvent, stateAfterwards: DerivedState) extends GameEvent

  implicit class GameEventSeqOps(seq: Seq[GameEvent]) {
    def sinceEvent[T <: GameObjectEvent : ClassTag]: Seq[GameEvent] = seq.takeRightUntil {
      case ResolvedEvent(e, _) if classTag[T].runtimeClass.isInstance(e) => true
      case _ => false
    }
    def getDecisions[T : ClassTag]: Seq[T] = {
      seq.view.ofType[Decision]
        .map(_.chosenOption)
        .mapCollect(_.asOptionalInstanceOf[T])
        .toSeq
    }
    def getDecision[T : ClassTag]: Option[T] = {
      seq.view.ofType[Decision]
        .map(_.chosenOption)
        .mapFind(_.asOptionalInstanceOf[T])
    }
  }
}
