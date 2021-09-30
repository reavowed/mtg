package mtg.game.start

import mtg.SpecWithGameStateManager
import mtg.game.start.mulligans.DrawAndMulliganAction
import mtg.game.state.GameActionResult
import mtg.game.state.history.LogEvent
import mtg.game.turns.StartNextTurnAction

class StartGameActionSpec extends SpecWithGameStateManager {
  "start game action" should {
    "initialize mulligans and turns" in {
      val pregameState = createGameState(gameObjectStateWithInitialLibrariesOnly, Nil)
      val result = StartGameAction.execute(pregameState)

      result.nextUpdates mustEqual Seq(
        DrawAndMulliganAction(players, 0),
        StartNextTurnAction(playerOne))
    }
    "log event" in {
      val pregameState = createGameState(gameObjectStateWithInitialLibrariesOnly, Nil)
      val result = StartGameAction.execute(pregameState)

      result.logEvent must beSome(LogEvent.Start(playerOne))
    }
  }
}
