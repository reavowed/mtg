package mtg.game.state

sealed trait GameActionExecutionState[+T] {
  def flatMap[S](f: T => GameAction[S]): GameActionExecutionState[S]
}

object GameActionExecutionState {
  // An execution state that can halt - i.e. end up with either a player choice or a game result
  sealed trait Halting[T] extends GameActionExecutionState[T]
  // An execution state that can be the child of another without further processing
  sealed trait Child[T] extends Halting[T] {
    override def flatMap[S](f: T => GameAction[S]): GameActionExecutionState[S] = {
      GameActionExecutionState.FlatMapped(this, f)
    }
  }
  sealed trait Interrupt extends GameActionExecutionState[Nothing] {
    override def flatMap[S](f: Nothing => GameAction[S]): GameActionExecutionState[S] = this
  }

  case class Value[T](value: T) extends GameActionExecutionState[T] {
    override def flatMap[S](f: T => GameAction[S]): GameActionExecutionState[S] = GameActionExecutionState.Action(f(value))
  }
  case class Backup(gameState: GameState) extends Interrupt
  case class Result(gameResult: GameResult) extends Interrupt with Halting[Nothing]
  case class FlatMapped[T, S](innerExecutionState: Child[T], f: T => GameAction[S]) extends Child[S]
  case class Action[T](gameAction: GameAction[T]) extends Child[T]
  case class DelegatingAction[T](gameAction: DelegatingGameAction[T], innerExecutionState: Child[T], initialGameState: GameState) extends Child[T]
}
