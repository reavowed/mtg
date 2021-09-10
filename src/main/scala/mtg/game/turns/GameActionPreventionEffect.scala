package mtg.game.turns

import mtg.effects.{ContinuousEffect, ContinuousObjectEffect}
import mtg.game.state.history.LogEvent
import mtg.game.state.{AutomaticGameAction, GameState}
import mtg.game.turns.turnEvents.BeginStepAction

trait GameActionPreventionEffect extends ContinuousEffect  {
  def checkEvent(gameAction: AutomaticGameAction, gameState: GameState): GameActionPreventionEffect.Result
}

object GameActionPreventionEffect {
  trait Simple extends GameActionPreventionEffect {
    def preventsEvent(gameAction: AutomaticGameAction, gameState: GameState): Boolean
    def checkEvent(gameAction: AutomaticGameAction, gameState: GameState): GameActionPreventionEffect.Result = {
      if (preventsEvent(gameAction, gameState))
        Result.Prevent(None)
      else
        Result.Allow
    }
  }
  trait SimpleObject extends ContinuousObjectEffect with Simple

  sealed trait Result
  object Result {
    case object Allow extends Result
    case class Prevent(logEvent: Option[LogEvent]) extends Result
  }

  object PreventFirstDrawStep extends GameActionPreventionEffect {
    override def checkEvent(gameAction: AutomaticGameAction, gameState: GameState): Result = {
      if (gameState.currentTurnNumber == 1 && gameAction == BeginStepAction(TurnStep.DrawStep)) {
        Result.Prevent(Some(LogEvent.SkipFirstDrawStep(gameState.activePlayer)))
      } else {
        Result.Allow
      }
    }
  }

  val fromRules: Seq[GameActionPreventionEffect] = Seq(PreventFirstDrawStep)
}
