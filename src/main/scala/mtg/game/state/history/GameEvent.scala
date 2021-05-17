package mtg.game.state.history

import mtg.game.PlayerIdentifier
import mtg.game.state.{ChoiceOption, GameObjectEvent}

import scala.reflect.{ClassTag, classTag}

sealed trait GameEvent
object GameEvent {
  case class Decision(chosenOption: ChoiceOption, playerIdentifier: PlayerIdentifier) extends GameEvent
  case class ResolvedEvent(event: GameObjectEvent) extends GameEvent

  implicit class GameEventSeqOps(seq: Seq[GameEvent]) {
    def sinceEvent[T <: GameObjectEvent : ClassTag]: Seq[GameEvent] = seq.takeRightUntil {
      case ResolvedEvent(e) if classTag[T].runtimeClass.isInstance(e) => true
      case _ => false
    }
  }
}
