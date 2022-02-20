package mtg.game.state

import mtg.core.zones.Zone
import mtg.core.{ObjectId, PlayerId}
import mtg.game.state.history.LogEvent

sealed trait GameUpdate

sealed trait GameAction[+T] extends GameUpdate
sealed trait CompoundGameAction[+T] extends GameAction[T]
trait ExecutableGameAction[+T] extends CompoundGameAction[T] {
  def execute()(implicit gameState: GameState): PartialGameActionResult[T]
}
sealed trait NewChoice[+T] extends CompoundGameAction[T] {
  def playerToAct: PlayerId
  def temporarilyVisibleZones: Seq[Zone] = Nil
  def temporarilyVisibleObjects: Seq[ObjectId] = Nil
}
trait RootGameAction extends ExecutableGameAction[RootGameAction]

case class PartiallyExecutedActionWithChild[T, S](rootAction: CompoundGameAction[T], childAction: GameAction[S], callback: (S, GameState) => PartialGameActionResult[T]) extends GameAction[T]
case class PartiallyExecutedActionWithValue[T, S](rootAction: CompoundGameAction[T], value: S, callback: (S, GameState) => PartialGameActionResult[T]) extends GameAction[T]

case class LogEventAction(logEvent: LogEvent) extends GameAction[Unit]

trait DirectChoice[T] extends NewChoice[T] {
  def handleDecision(serializedDecision: String)(implicit gameState: GameState): Option[T]
}
object DirectChoice {
  trait WithParser[T] extends DirectChoice[T] {
    def getParser()(implicit gameState: GameState): PartialFunction[String, T]
    override def handleDecision(serializedDecision: String)(implicit gameState: GameState): Option[T] = getParser().lift(serializedDecision)
  }
}

sealed trait OldGameUpdate extends GameUpdate
trait InternalGameAction extends OldGameUpdate {
  def execute(gameState: GameState): GameActionResult
  def canBeReverted: Boolean
}

case class Decision(resultingActions: Seq[InternalGameAction])
object Decision {
  implicit def single(action: InternalGameAction): Decision = Decision(Seq(action))
  implicit def singleOption(action: InternalGameAction): Option[Decision] = Some(Decision(Seq(action)))
  implicit def multiple(actions: Seq[InternalGameAction]): Decision = Decision(actions)
  implicit def chain[T, S](f: T => S)(implicit g: S => Decision): T => Decision = t => g(f(t))
}

case class WrappedOldUpdates(oldUpdates: OldGameUpdate*) extends GameAction[Unit]

object GameAction {
  implicit def logEventAsAction(logEvent: LogEvent): GameAction[Unit] = LogEventAction(logEvent)
}

