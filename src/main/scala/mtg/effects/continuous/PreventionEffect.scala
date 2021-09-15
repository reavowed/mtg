package mtg.effects.continuous

import mtg.effects.ContinuousEffect
import mtg.game.ObjectId
import mtg.game.state.history.LogEvent
import mtg.game.state.{GameState, InternalGameAction}
import mtg.game.turns.TurnStep
import mtg.game.turns.turnEvents.BeginStepEvent

trait PreventionEffect extends ContinuousEffect {
  def checkAction(action: InternalGameAction, gameState: GameState): PreventionEffect.Result
}

object PreventionEffect {
  sealed trait Result
  object Result {
    case object Allow extends Result
    case class Prevent(logEvent: Option[LogEvent]) extends Result
  }

  trait SimpleCheck extends PreventionEffect {
    def shouldPreventAction(action: InternalGameAction, gameState: GameState): Boolean

    override def checkAction(action: InternalGameAction, gameState: GameState): Result = {
      if (shouldPreventAction(action, gameState)) {
        Result.Prevent(None)
      } else {
        Result.Allow
      }
    }
  }

  object PreventFirstDrawStep extends PreventionEffect {
    override def affectedObjects: Set[ObjectId] = Set.empty
    override def checkAction(action: InternalGameAction, gameState: GameState): Result = {
      if (gameState.currentTurnNumber == 1 && action == BeginStepEvent(TurnStep.DrawStep)) {
        Result.Prevent(Some(LogEvent.SkipFirstDrawStep(gameState.activePlayer)))
      } else {
        Result.Allow
      }
    }
  }

  val fromRules: Seq[PreventionEffect] = Seq(PreventFirstDrawStep)
}
