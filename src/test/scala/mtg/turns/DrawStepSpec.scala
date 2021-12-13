package mtg.turns

import mtg.SpecWithGameStateManager
import mtg.game.state.history.{LogEvent, PhaseHistory, PhaseHistoryWithSteps, TimestampedLogEvent}
import mtg.game.turns.turnEvents.BeginStepEvent
import mtg.game.turns.{TurnPhase, TurnStep}
import org.specs2.matcher.Matcher

class DrawStepSpec extends SpecWithGameStateManager {

  def haveHistoryForStep(turnStep: TurnStep): Matcher[PhaseHistory] = { phaseHistory: PhaseHistory =>
    (phaseHistory.asOptionalInstanceOf[PhaseHistoryWithSteps].exists(_.steps.exists(_.step == turnStep)), "", "")
  }
  def logEvent(logEvent: LogEvent): Matcher[TimestampedLogEvent] = { timestampedLogEvent: TimestampedLogEvent =>
    (timestampedLogEvent.logEvent == logEvent, "", "")
  }

  "draw step" should {
    "be skipped for first turn" in {
      val initialState = gameObjectStateWithInitialLibrariesAndHands
      val manager = createGameStateManagerAtStartOfFirstTurn(initialState)

      manager.passUntilTurn(1)
      manager.passUntilPhase(TurnPhase.PrecombatMainPhase)

      val finalState = manager.gameState

      playerOne.hand(finalState).size mustEqual 7
      finalState.gameHistory.gameEventsThisTurn.actions must not(contain(BeginStepEvent(TurnStep.DrawStep)))
      finalState.gameHistory.logEvents must not(contain(logEvent(LogEvent.DrawForTurn(playerOne))))
      finalState.gameHistory.logEvents must contain(logEvent(LogEvent.SkipFirstDrawStep(playerOne)))
    }
    "not be skipped for second turn" in {
      val initialState = gameObjectStateWithInitialLibrariesAndHands
      val manager = createGameStateManagerAtStartOfFirstTurn(initialState)

      manager.passUntilTurn(2)
      manager.passUntilPhase(TurnPhase.PrecombatMainPhase)

      val finalState = manager.gameState

      playerTwo.hand(finalState).size mustEqual 8
      finalState.gameHistory.gameEventsThisTurn.actions must contain(BeginStepEvent(TurnStep.DrawStep))
      finalState.gameHistory.logEvents must contain(logEvent(LogEvent.DrawForTurn(playerTwo)))
      finalState.gameHistory.logEvents must not(contain(logEvent(LogEvent.SkipFirstDrawStep(playerTwo))))
    }
  }
}
