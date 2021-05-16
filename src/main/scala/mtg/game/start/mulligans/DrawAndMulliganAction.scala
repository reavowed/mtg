package mtg.game.start.mulligans

import mtg.game.PlayerIdentifier
import mtg.game.state.{GameActionManager, GameAction, GameState}

case class DrawAndMulliganAction(playersToDrawAndMulligan: Seq[PlayerIdentifier], mulligansSoFar: Int) extends GameActionManager {
  override def execute(currentGameState: GameState): Seq[GameAction] = {
    val drawActions = Seq(DrawStartingHandsEvent(playersToDrawAndMulligan))
    if (mulligansSoFar < currentGameState.gameData.startingHandSize) {
      val mulliganChoices = playersToDrawAndMulligan.map(MulliganChoice(_, mulligansSoFar))
      val executeMulligansAction = ExecuteMulligansAction(mulligansSoFar)
      drawActions ++ mulliganChoices :+ executeMulligansAction
    } else {
      drawActions ++ playersToDrawAndMulligan.map(ReturnCardsToLibraryChoice(_, currentGameState.gameData.startingHandSize))
    }
  }
}
