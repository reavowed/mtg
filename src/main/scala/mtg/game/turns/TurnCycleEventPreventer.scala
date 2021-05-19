package mtg.game.turns

import mtg.game.state.{GameState, TurnCycleEvent}
import mtg.game.state.history.LogEvent

abstract class TurnCycleEventPreventer {
  def checkEvent(turnCycleEvent: TurnCycleEvent, gameState: GameState): TurnCycleEventPreventer.Result
}

object TurnCycleEventPreventer {
  sealed trait Result
  object Result {
    case object Allow extends Result
    case class Prevent(logEvent: Option[LogEvent]) extends Result
  }

  object PreventFirstDrawStep extends TurnCycleEventPreventer {
    override def checkEvent(turnCycleEvent: TurnCycleEvent, gameState: GameState): Result = {
      if (gameState.currentTurnNumber == 1 && turnCycleEvent == BeginStepEvent(TurnStep.DrawStep)) {
        Result.Prevent(Some(LogEvent.SkipFirstDrawStep(gameState.activePlayer)))
      } else {
        Result.Allow
      }
    }
  }

  val fromRules: Seq[TurnCycleEventPreventer] = Seq(PreventFirstDrawStep)
}
