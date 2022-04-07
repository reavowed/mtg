package mtg.game.turns.turnEvents

import mtg.actions.EmptyManaPoolsAction
import mtg.game.state.{DelegatingGameAction, GameAction, GameState}
import mtg.game.turns.TurnPhase

case class ExecutePhase(phase: TurnPhase) extends DelegatingGameAction[Unit] {
  override def delegate(implicit gameState: GameState): GameAction[Unit] = {
    for {
      _ <- phase.actions.traverse
      _ <- EmptyManaPoolsAction
    } yield ()
  }
}
