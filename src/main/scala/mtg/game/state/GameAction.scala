package mtg.game.state

import mtg.core.zones.Zone
import mtg.core.{ObjectId, PlayerId}
import mtg.game.objects.GameObjectState
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

case class LogEventAction(logEvent: LogEvent) extends GameAction[Unit]

sealed trait GameObjectAction[T] extends GameAction[T]
trait DirectGameObjectAction[T] extends GameObjectAction[Option[T]] {
  def execute(implicit gameState: GameState): DirectGameObjectAction.Result[T]
  def canBeReverted: Boolean
  def getLogEvent(gameState: GameState): Option[LogEvent] = None
}
object DirectGameObjectAction {
  sealed trait Result[+T]
  case class Happened[T](value: T, gameObjectState: GameObjectState) extends Result[T]
  object DidntHappen extends Result[Nothing]
  object Result {
    implicit def resultFromNothing(none: None.type): Result[Nothing] = DidntHappen
    implicit def resultFromValue[T](value: T)(implicit gameState: GameState): Result[T] = Happened(value, gameState.gameObjectState)
    implicit def resultFromTuple[T](tuple: (T, GameObjectState)): Result[T] = (Happened.apply[T] _).tupled(tuple)
    implicit def resultFromTupleOption[T](tupleOption: Option[(T, GameObjectState)]): Result[T] = tupleOption.map(resultFromTuple).getOrElse(DidntHappen)
    implicit def unitResultFromGameObjectState(gameObjectState: GameObjectState): Result[Unit] = Happened((), gameObjectState)
    implicit def unitResultFromOptionalGameObjectState(gameObjectStateOption: Option[GameObjectState])(implicit gameState: GameState): Result[Unit] = {
      gameObjectStateOption.map(unitResultFromGameObjectState).getOrElse(DidntHappen)
    }
  }
}
trait DelegatingGameObjectAction extends GameObjectAction[Unit] {
  def delegate(implicit gameState: GameState): Seq[GameObjectAction[_]]
  protected implicit def seqFromSingle(action: GameObjectAction[_]): Seq[GameObjectAction[_]] = Seq(action)
}
case class GameResultAction(gameResult: GameResult) extends GameObjectAction[Nothing]

case class PartiallyExecutedGameObjectAction(
    gameObjectAction: DelegatingGameObjectAction,
    remainingChildActions: Seq[GameObjectAction[_]],
    initialGameState: GameState)
  extends GameObjectAction[Unit]


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
  implicit def constant[T](value: T): GameAction[T] = ConstantAction(value)
  implicit def getterAsAction[T](constructor: GameState => T): GameAction[T] = CalculatedGameAction(constructor.andThen(ConstantAction(_)))
  implicit def constructorAsAction[T](constructor: GameState => GameAction[T]): GameAction[T] = CalculatedGameAction(constructor)
  implicit def anyActionToUnitAction(action: GameAction[_]): GameAction[Unit] = action.map(_ => ())
}
