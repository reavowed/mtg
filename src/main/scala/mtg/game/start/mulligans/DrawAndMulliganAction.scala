package mtg.game.start.mulligans

import mtg.game.PlayerId
import mtg.game.state.history.LogEvent
import mtg.game.state.{GameAction, GameState, InternalGameAction, GameActionResult}

case class DrawAndMulliganAction(playersToDrawAndMulligan: Seq[PlayerId], mulligansSoFar: Int) extends InternalGameAction {
  override def execute(currentGameState: GameState): GameActionResult = {
    val drawActions = Seq(DrawStartingHandsAction(playersToDrawAndMulligan))
    if (mulligansSoFar < currentGameState.gameData.startingHandSize) {
      val mulliganChoices = playersToDrawAndMulligan.map(MulliganChoice(_, mulligansSoFar))
      val executeMulligansAction = ExecuteMulligansAction(mulligansSoFar)
      drawActions ++ mulliganChoices :+ executeMulligansAction
    } else {
      drawActions ++ playersToDrawAndMulligan.map(ReturnCardsToLibraryChoice(_, currentGameState.gameData.startingHandSize))
    }
  }
}
