package mtg.game.start

import mtg.SpecWithGameStateManager
import mtg.game.state.history.LogEvent

class StartGameActionSpec extends SpecWithGameStateManager {
  "start game action" should {
    "initialize mulligans and turns" in {
      val gameStateAfterAction = runAction(DrawOpeningHandsAction, gameObjectStateWithInitialLibrariesOnly)

      gameStateAfterAction.allCurrentActions.head mustEqual MulligansAction(players, 0)
    }
    "log event" in {
      val gameStateAfterAction = runAction(DrawOpeningHandsAction, gameObjectStateWithInitialLibrariesOnly)

      gameStateAfterAction.gameHistory.logEvents.lastOption must beSome(LogEvent.Start(playerOne))
    }
  }
}
