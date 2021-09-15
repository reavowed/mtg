package mtg.game.start.mulligans

import mtg.game.PlayerId
import mtg.game.state.{GameState, InternalGameAction, GameActionResult}

case class DrawAndMulliganAction(playersToDrawAndMulligan: Seq[PlayerId], mulligansSoFar: Int) extends InternalGameAction {
  override def execute(gameState: GameState): GameActionResult = {
    val drawActions = Seq(DrawStartingHandsEvent(playersToDrawAndMulligan, mulligansSoFar))
    if (mulligansSoFar < gameState.gameData.startingHandSize) {
      val mulliganChoices = playersToDrawAndMulligan.map(MulliganChoice(_, mulligansSoFar))
      val executeMulligansAction = ExecuteMulligansAction(mulligansSoFar)
      drawActions ++ mulliganChoices :+ executeMulligansAction
    } else {
      drawActions ++ playersToDrawAndMulligan.map(ReturnCardsToLibraryChoice(_, gameState.gameData.startingHandSize, gameState))
    }
  }
  override def canBeReverted: Boolean = false
}
