package mtg.turns

import mtg.SpecWithGameStateManager
import mtg.game.state.history.{LogEvent, TimestampedLogEvent}
import mtg.game.turns.TurnStep.DrawStep
import mtg.game.turns.turnEvents.BeginStepAction
import mtg.game.turns.{StartNextTurnAction, TurnPhase, TurnStep}
import org.specs2.matcher.Matcher

class DrawStepSpec extends SpecWithGameStateManager {

  def logEvent(logEvent: LogEvent): Matcher[TimestampedLogEvent] = { timestampedLogEvent: TimestampedLogEvent =>
    (timestampedLogEvent.logEvent == logEvent, "", "")
  }

  "draw step" should {
    "be skipped for first turn" in {
      val initialState = gameObjectStateWithInitialLibrariesAndHands
      val manager = createGameStateManager(initialState, StartNextTurnAction(playerOne))

      manager.passUntilTurn(1)
      manager.passUntilPhase(TurnPhase.PrecombatMainPhase)

      val finalState = manager.currentGameState

      playerOne.hand(finalState).size mustEqual 7
      finalState.eventsThisTurn must not(contain(BeginStepAction(DrawStep)))
      finalState.gameHistory.logEvents must not(contain(logEvent(LogEvent.DrawForTurn(playerOne))))
      finalState.gameHistory.logEvents must contain(logEvent(LogEvent.SkipFirstDrawStep(playerOne)))
    }
    "not be skipped for second turn" in {
      val initialState = gameObjectStateWithInitialLibrariesAndHands
      val manager = createGameStateManager(initialState, StartNextTurnAction(playerOne))

      manager.passUntilTurn(2)
      manager.passUntilPhase(TurnPhase.PrecombatMainPhase)

      val finalState = manager.currentGameState

      playerTwo.hand(finalState).size mustEqual 8
      finalState.eventsThisTurn must contain(BeginStepAction(DrawStep))
      finalState.gameHistory.logEvents must contain(logEvent(LogEvent.DrawForTurn(playerTwo)))
      finalState.gameHistory.logEvents must not(contain(logEvent(LogEvent.SkipFirstDrawStep(playerTwo))))
    }
  }
}
