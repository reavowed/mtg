package mtg.game.state

sealed trait NewGameActionResult[+T]
object NewGameActionResult {
  sealed trait Terminal[+T] extends NewGameActionResult[T]
  sealed trait Partial[+T] extends NewGameActionResult[T]
  sealed trait Halting extends Terminal[Nothing] with Partial[Nothing]

  case class Value[T](value: T) extends Terminal[T] with Partial[T]
  case class NewAction[T](action: GameAction[T]) extends Terminal[T]
  case class Backup(gameState: GameState) extends Halting
  case class GameOver(gameResult: GameResult) extends Halting
  case class Delegated[T, S](child: GameAction[S], callback: (S, GameState) => NewGameActionResult.Partial[T]) extends NewGameActionResult.Partial[T]

  object Delegated {
    def directly[T](child: GameAction[T]): Delegated[T, T] = Delegated(child, (v, _) => Value(v))
    def toChildren[T](children: Seq[GameAction[T]]): Partial[Seq[T]] = {
      def executeNext(remainingChildActions: Seq[GameAction[T]], resultsSoFar: Seq[T]): Partial[Seq[T]] = {
        remainingChildActions match {
          case head +: tail =>
            Delegated(head, (s: T, _) => executeNext(tail, resultsSoFar :+ s))
          case Nil =>
            Value(resultsSoFar)
        }
      }
      executeNext(children, Nil)
    }
    def toChildren[T, S](childActions: Seq[GameAction[S]], callback: (Seq[S], GameState) => Partial[T])(implicit gameState: GameState): Partial[T] = {
      def executeNext(remainingChildActions: Seq[GameAction[S]], resultsSoFar: Seq[S], gameState: GameState): Partial[T] = {
        remainingChildActions match {
          case head +: tail =>
            Delegated(head, (s: S, gameState) => executeNext(tail, resultsSoFar :+ s, gameState))
          case Nil =>
            callback(resultsSoFar, gameState)
        }
      }
      executeNext(childActions, Nil, gameState)
    }
    def childThenValue[T](child: GameAction[Any], value: T)(implicit gameState: GameState): Partial[T] = {
      childrenThenValue(Seq(child), value)
    }
    def childrenThenValue[T](children: Seq[GameAction[Any]], value: T)(implicit gameState: GameState): Partial[T] = {
      toChildren(children, (_: Seq[Any], _) => Value(value))
    }
  }
}
