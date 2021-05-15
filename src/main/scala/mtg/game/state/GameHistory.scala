package mtg.game.state

import mtg.events.Event
import mtg.game.PlayerIdentifier

import scala.reflect.{ClassTag, classTag}

case class GameHistory(preGameEvents: Seq[GameEvent]) {
  def addEvent(event: GameEvent): GameHistory = copy(preGameEvents = preGameEvents :+ event)
}

object GameHistory {
  val empty = GameHistory(Nil)
}

sealed trait GameEvent
object GameEvent {
  case class Decision[TOption <: Option](chosenOption: TOption, playerIdentifier: PlayerIdentifier) extends GameEvent
  case class ResolvedEvent(event: Event) extends GameEvent

  implicit class GameEventSeqOps(seq: Seq[GameEvent]) {
    def sinceEvent[T <: Event : ClassTag]: Seq[GameEvent] = seq.takeRightUntil {
      case ResolvedEvent(e) if classTag[T].runtimeClass.isInstance(e) => true
      case _ => false
    }
  }
}
