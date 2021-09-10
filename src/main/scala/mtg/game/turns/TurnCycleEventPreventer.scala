package mtg.game.turns

import mtg.game.state.{GameState, TurnCycleAction}
import mtg.game.state.history.LogEvent
import mtg.game.turns.turnEvents.BeginStepAction

abstract class TurnCycleEventPreventer {
  def checkEvent(turnCycleEvent: TurnCycleAction, gameState: GameState): TurnCycleEventPreventer.Result
}

object TurnCycleEventPreventer {
  sealed trait Result
  object Result {
    case object Allow extends Result
    case class Prevent(logEvent: Option[LogEvent]) extends Result
  }

  object PreventFirstDrawStep extends TurnCycleEventPreventer {
    override def checkEvent(turnCycleEvent: TurnCycleAction, gameState: GameState): Result = {
      if (gameState.currentTurnNumber == 1 && turnCycleEvent == BeginStepAction(TurnStep.DrawStep)) {
        Result.Prevent(Some(LogEvent.SkipFirstDrawStep(gameState.activePlayer)))
      } else {
        Result.Allow
      }
    }
  }

  val fromRules: Seq[TurnCycleEventPreventer] = Seq(PreventFirstDrawStep)
}
