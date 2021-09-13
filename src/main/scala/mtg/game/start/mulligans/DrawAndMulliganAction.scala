package mtg.game.start.mulligans

import mtg.game.PlayerId
import mtg.game.state.{GameState, InternalGameAction, InternalGameActionResult}

case class DrawAndMulliganAction(playersToDrawAndMulligan: Seq[PlayerId], mulligansSoFar: Int) extends InternalGameAction {
  override def execute(currentGameState: GameState): InternalGameActionResult = {
    val drawActions = Seq(DrawStartingHandsEvent(playersToDrawAndMulligan))
    if (mulligansSoFar < currentGameState.gameData.startingHandSize) {
      val mulliganChoices = playersToDrawAndMulligan.map(MulliganChoice(_, mulligansSoFar))
      val executeMulligansAction = ExecuteMulligansAction(mulligansSoFar)
      drawActions ++ mulliganChoices :+ executeMulligansAction
    } else {
      drawActions ++ playersToDrawAndMulligan.map(ReturnCardsToLibraryChoice(_, currentGameState.gameData.startingHandSize))
    }
  }
  override def canBeReverted: Boolean = false
}
