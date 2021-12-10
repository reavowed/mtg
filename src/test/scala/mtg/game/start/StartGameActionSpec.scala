package mtg.game.start

import mtg.SpecWithGameStateManager
import mtg.game.state.history.LogEvent

class StartGameActionSpec extends SpecWithGameStateManager {
  "start game action" should {
    "log event" in {
      val gameStateAfterAction = runAction(StartGameAction, gameObjectStateWithInitialLibrariesOnly)

      gameStateAfterAction.gameHistory.logEvents.lastOption.map(_.logEvent) must beSome(LogEvent.Start(playerOne))
    }
    "trigger mulligans" in {
      val gameStateAfterAction = runAction(StartGameAction, gameObjectStateWithInitialLibrariesOnly)

      gameStateAfterAction.allCurrentActions.head mustEqual MulligansAction(players, 0)
    }
  }
}
