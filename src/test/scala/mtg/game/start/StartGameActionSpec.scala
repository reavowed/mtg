package mtg.game.start

import mtg.SpecWithGameStateManager
import mtg.game.start.mulligans.DrawAndMulliganAction
import mtg.game.state.LogEvent
import mtg.game.turns.StartNextTurnAction

class StartGameActionSpec extends SpecWithGameStateManager {
  "start game action" should {
    "initialize mulligans and turns" in {
      val pregameState = createGameState(gameObjectStateWithInitialLibrariesOnly, Nil)
      val (actions, _) = StartGameAction.execute(pregameState)

      actions mustEqual Seq(
        DrawAndMulliganAction(players, 0),
        StartNextTurnAction(playerOne))
    }
    "log event" in {
      val pregameState = createGameState(gameObjectStateWithInitialLibrariesOnly, Nil)
      val (_, logEvent) = StartGameAction.execute(pregameState)

      logEvent must beSome(LogEvent.Start(playerOne))
    }
  }
}
