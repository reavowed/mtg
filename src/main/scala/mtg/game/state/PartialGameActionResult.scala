package mtg.game.state

sealed trait PartialGameActionResult[+T]
object PartialGameActionResult {
  case class Value[T](value: T) extends PartialGameActionResult[T]
  case class ChildWithCallback[T, S](child: GameAction[S], callback: (S, GameState) => PartialGameActionResult[T]) extends PartialGameActionResult[T]
  case class Backup(gameState: GameState) extends PartialGameActionResult[Nothing]
  case class GameOver(gameResult: GameResult) extends PartialGameActionResult[Nothing]

  implicit def valueAsPartialGameActionResult[T](value: T): PartialGameActionResult[T] = Value(value)

  def child[T](child: GameAction[T]): PartialGameActionResult[T] = ChildWithCallback(child, (v: T, _) => v)
  def children[T](children: GameAction[T]*): PartialGameActionResult[Seq[T]] = {
    def executeNext(remainingChildActions: Seq[GameAction[T]], resultsSoFar: Seq[T]): PartialGameActionResult[Seq[T]] = {
      remainingChildActions match {
        case head +: tail =>
          ChildWithCallback(head, (s: T, _) => executeNext(tail, resultsSoFar :+ s))
        case Nil =>
          Value(resultsSoFar)
      }
    }
    executeNext(children, Nil)
  }
  def childrenWithCallback[T, S](childActions: Seq[GameAction[S]], callback: (Seq[S], GameState) => PartialGameActionResult[T])(implicit gameState: GameState): PartialGameActionResult[T] = {
    def executeNext(remainingChildActions: Seq[GameAction[S]], resultsSoFar: Seq[S], gameState: GameState): PartialGameActionResult[T] = {
      remainingChildActions match {
        case head +: tail =>
          ChildWithCallback(head, (s: S, gameState) => executeNext(tail, resultsSoFar :+ s, gameState))
        case Nil =>
          callback(resultsSoFar, gameState)
      }
    }
    executeNext(childActions, Nil, gameState)
  }
  def childThenValue[T](child: GameAction[Any], value: T)(implicit gameState: GameState): PartialGameActionResult[T] = {
    childrenThenValue(Seq(child), value)
  }
  def childrenThenValue[T](children: Seq[GameAction[Any]], value: T)(implicit gameState: GameState): PartialGameActionResult[T] = {
    childrenWithCallback(children, (_: Seq[Any], _) => Value(value))
  }
}
