package mtg.game.state

import mtg.core.zones.Zone
import mtg.core.{ObjectId, PlayerId}
import mtg.game.state.history.LogEvent

sealed trait GameAction[+T] {
  def map[S](f: T => S): GameAction[S] = {
    flatMap(f.andThen(ConstantAction.apply))
  }
  def flatMap[S](f: T => GameAction[S]): GameAction[S] = {
    FlatMappedGameAction(this, f)
  }
  def andThen[S](nextAction: GameAction[S]): GameAction[S] = flatMap(_ => nextAction)
}

trait ExecutableGameAction[+T] extends GameAction[T] {
  def execute()(implicit gameState: GameState): PartialGameActionResult[T]
}
trait DelegatingGameAction[+T] extends GameAction[T] {
  def delegate(implicit gameState: GameState): GameAction[T]
}
trait RootGameAction extends DelegatingGameAction[RootGameAction]

case class CalculatedGameAction[T](f: GameState => GameAction[T]) extends GameAction[T]
case class ConstantAction[T](value: T) extends GameAction[T] {
  override def map[S](f: T => S): GameAction[S] = ConstantAction(f(value))
  override def flatMap[S](f: T => GameAction[S]): GameAction[S] = f(value)
}
case class FlatMappedGameAction[T, S](base: GameAction[T], f: T => GameAction[S]) extends GameAction[S] {
  override def map[U](g: S => U): GameAction[U] = FlatMappedGameAction(base, (t: T) => FlatMappedGameAction(f(t), g.andThen(ConstantAction.apply)))
  override def flatMap[U](g: S => GameAction[U]): GameAction[U] = FlatMappedGameAction(base, (t: T) => FlatMappedGameAction(f(t), g))
}

trait Choice[+T] extends GameAction[T] {
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
case class PartiallyExecutedActionWithDelegate[T](rootAction: DelegatingGameAction[T], childAction: GameAction[T]) extends GameAction[T]
case class PartiallyExecutedActionWithFlatMap[T, S](rootAction: DelegatingGameAction[T], childAction: GameAction[S], f: S => GameAction[T]) extends GameAction[T]
case class PartiallyExecutedActionWithChild[T, S](rootAction: GameAction[T], childAction: GameAction[S], callback: (S, GameState) => PartialGameActionResult[T]) extends GameAction[T]
case class PartiallyExecutedActionWithValue[T, S](rootAction: GameAction[T], value: S, callback: (S, GameState) => PartialGameActionResult[T]) extends GameAction[T]

case class LogEventAction(logEvent: LogEvent) extends GameAction[Unit]

trait InternalGameAction extends GameAction[Unit] {
  def execute(gameState: GameState): GameActionResult
  def canBeReverted: Boolean
  def getLogEvent(gameState: GameState): Option[LogEvent] = None
}

case class WrappedOldUpdates(oldUpdates: InternalGameAction*) extends GameAction[Unit]

object GameAction {
  implicit class SeqExtensions[T](seq: Seq[GameAction[T]]) {
    def traverse: GameAction[Seq[T]] = seq match {
      case Nil => ConstantAction(Nil)
      case head +: tail => for {
        t <- head
        results <- tail.traverse
      } yield t +: results
    }
  }
  implicit def valueAsAction[T](value: T): GameAction[T] = ConstantAction(value)
  implicit def getterAsAction[T](constructor: GameState => T): GameAction[T] = CalculatedGameAction(constructor.andThen(ConstantAction(_)))
  implicit def constructorAsAction[T](constructor: GameState => GameAction[T]): GameAction[T] = CalculatedGameAction(constructor)
  implicit def anyActionToUnitAction(action: GameAction[_]): GameAction[Unit] = action.map(_ => ())
}
