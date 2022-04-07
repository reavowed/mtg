package mtg.game.turns.turnEvents

import mtg.actions.EmptyManaPoolsAction
import mtg.game.state.{DelegatingGameAction, GameAction, GameState}
import mtg.game.turns.TurnStep

case class ExecuteStep(step: TurnStep) extends DelegatingGameAction[Unit] {
  override def delegate(implicit gameState: GameState): GameAction[Unit] = {
    for {
      _ <- BeginStep(step)
      _ <- step.actions.traverse
      _ <- EmptyManaPoolsAction
    } yield ()
  }
}

case class BeginStep(step: TurnStep) extends DelegatingGameAction[Unit] {
  override def delegate(implicit gameState: GameState): GameAction[Unit] = ()
}
