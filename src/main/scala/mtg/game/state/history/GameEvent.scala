package mtg.game.state.history

import mtg.game.PlayerIdentifier
import mtg.game.state.{DerivedState, GameObjectEvent}

import scala.reflect.{ClassTag, classTag}

sealed trait GameEvent
object GameEvent {
  case class Decision(chosenOption: AnyRef, playerIdentifier: PlayerIdentifier) extends GameEvent
  case class ResolvedEvent(event: GameObjectEvent, stateAfterwards: DerivedState) extends GameEvent

  implicit class GameEventSeqOps(seq: Seq[GameEvent]) {
    def sinceEvent[T <: GameObjectEvent : ClassTag]: Seq[GameEvent] = seq.takeRightUntil {
      case ResolvedEvent(e, _) if classTag[T].runtimeClass.isInstance(e) => true
      case _ => false
    }
  }
}
