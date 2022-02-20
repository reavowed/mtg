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
trait RootGameAction extends ExecutableGameAction[RootGameAction]

trait Choice[+T] extends CompoundGameAction[T] {
  def playerToAct: PlayerId
  def handleDecision(serializedDecision: String)(implicit gameState: GameState): Option[T]
  def temporarilyVisibleZones: Seq[Zone] = Nil
  def temporarilyVisibleObjects: Seq[ObjectId] = Nil
}
object Choice {
  trait WithParser[T] extends Choice[T] {
    def getParser()(implicit gameState: GameState): PartialFunction[String, T]
    override def handleDecision(serializedDecision: String)(implicit gameState: GameState): Option[T] = getParser().lift(serializedDecision)
  }
}

case class PartiallyExecutedActionWithChild[T, S](rootAction: CompoundGameAction[T], childAction: GameAction[S], callback: (S, GameState) => PartialGameActionResult[T]) extends GameAction[T]
case class PartiallyExecutedActionWithValue[T, S](rootAction: CompoundGameAction[T], value: S, callback: (S, GameState) => PartialGameActionResult[T]) extends GameAction[T]

case class LogEventAction(logEvent: LogEvent) extends GameAction[Unit]

trait InternalGameAction extends GameUpdate {
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

case class WrappedOldUpdates(oldUpdates: InternalGameAction*) extends GameAction[Unit]

object GameAction {
  implicit def logEventAsAction(logEvent: LogEvent): GameAction[Unit] = LogEventAction(logEvent)
}

