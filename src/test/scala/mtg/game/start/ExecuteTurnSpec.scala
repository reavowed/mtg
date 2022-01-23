package mtg.game.start

import mtg.SpecWithGameStateManager
import mtg.game.state.history.LogEvent
import mtg.game.turns.Turn
import mtg.game.turns.turnEvents.ExecuteTurn

class ExecuteTurnSpec extends SpecWithGameStateManager {
  "execute turn action" should {
    "pass turn from first to second player" in {
      val manager = createGameStateManager(emptyGameObjectState, ExecuteTurn(Turn(1, playerOne)))

      manager.passUntilTurn(2)

      manager.gameState.currentTurn must beSome(Turn(2, playerTwo))
    }

    "pass turn from second to first player" in {
      val manager = createGameStateManager(emptyGameObjectState, ExecuteTurn(Turn(2, playerTwo)))

      manager.passUntilTurn(3)

      manager.gameState.currentTurn must beSome(Turn(3, playerOne))
    }

    "log event" in {
      val manager = createGameStateManager(emptyGameObjectState, ExecuteTurn(Turn(1, playerOne)))

      manager.gameState.gameHistory.logEvents.map(_.logEvent) must contain(LogEvent.NewTurn(Turn(1, playerOne)))
    }
  }
}
