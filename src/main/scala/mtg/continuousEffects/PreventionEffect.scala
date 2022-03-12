package mtg.continuousEffects

import mtg.game.state.history.LogEvent
import mtg.game.state.{GameState, GameUpdate}
import mtg.game.turns.TurnStep
import mtg.game.turns.turnEvents.ExecuteStep

trait PreventionEffect extends ContinuousEffect {
  def checkAction(action: GameUpdate, gameState: GameState): PreventionEffect.Result
}

object PreventionEffect {
  sealed trait Result
  object Result {
    case object Allow extends Result
    case class Prevent(logEvent: Option[LogEvent]) extends Result
  }

  trait SimpleCheck extends PreventionEffect {
    def shouldPreventAction(action: GameUpdate, gameState: GameState): Boolean

    override def checkAction(action: GameUpdate, gameState: GameState): Result = {
      if (shouldPreventAction(action, gameState)) {
        Result.Prevent(None)
      } else {
        Result.Allow
      }
    }
  }

  object PreventFirstDrawStep extends PreventionEffect {
    override def checkAction(action: GameUpdate, gameState: GameState): Result = {
      if (gameState.currentTurnNumber == 1 && action == ExecuteStep(TurnStep.DrawStep)) {
        Result.Prevent(Some(LogEvent.SkipFirstDrawStep(gameState.activePlayer)))
      } else {
        Result.Allow
      }
    }
  }

  val fromRules: Seq[PreventionEffect] = Seq(PreventFirstDrawStep)
}
