package mtg.game.start

import mtg.SpecWithGameObjectState
import mtg.game.GameData
import mtg.game.state.{GameHistory, GameState}
import mtg.game.turns.{PriorityChoice, StartNextTurnAction}

class StartNextTurnActionSpec extends SpecWithGameObjectState {
  "start next turn action" should {
    "pass turn from first to second player" in {
      val gameState = GameState(GameData.initial(Seq(playerOne, playerTwo)), emptyGameObjectState, GameHistory.empty, Nil)

      val gameActions = StartNextTurnAction(playerOne).execute(gameState)

      gameActions mustEqual Seq(PriorityChoice(Seq(playerOne, playerTwo)), StartNextTurnAction(playerTwo))
    }

    "pass turn from second to first player" in {
      val gameState = GameState(GameData.initial(Seq(playerOne, playerTwo)), emptyGameObjectState, GameHistory.empty, Nil)

      val gameActions = StartNextTurnAction(playerTwo).execute(gameState)

      gameActions mustEqual Seq(PriorityChoice(Seq(playerTwo, playerOne)), StartNextTurnAction(playerOne))
    }
  }
}
