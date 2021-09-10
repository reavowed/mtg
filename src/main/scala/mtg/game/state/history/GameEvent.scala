package mtg.game.state.history

import mtg._
import mtg.game.PlayerId
import mtg.game.state.{DerivedState, GameObjectAction}

import scala.reflect.{ClassTag, classTag}

sealed trait GameEvent
object GameEvent {
  case class Decision(chosenOption: AnyRef, playerIdentifier: PlayerId) extends GameEvent
  case class ResolvedEvent(event: GameObjectAction, stateAfterwards: DerivedState) extends GameEvent

  implicit class GameEventIterableOps(events: Iterable[GameEvent]) {
    def sinceEvent[T <: GameObjectAction : ClassTag]: Iterable[GameEvent] = events.takeWhile {
      case ResolvedEvent(e, _) if classTag[T].runtimeClass.isInstance(e) => true
      case _ => false
    }
    def getDecisions[T : ClassTag]: Iterable[T] = {
      events.ofType[Decision]
        .map(_.chosenOption)
        .mapCollect(_.asOptionalInstanceOf[T])
    }
    def getDecision[T : ClassTag]: Option[T] = {
      events.ofType[Decision]
        .map(_.chosenOption)
        .mapFind(_.asOptionalInstanceOf[T])
    }
  }
}
