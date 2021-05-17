package mtg.game.start.mulligans

import mtg.game.PlayerIdentifier
import mtg.game.state.{GameAction, InternalGameAction, GameState, LogEvent}

case class DrawAndMulliganAction(playersToDrawAndMulligan: Seq[PlayerIdentifier], mulligansSoFar: Int) extends InternalGameAction {
  override def execute(currentGameState: GameState): (Seq[GameAction], Option[LogEvent]) = {
    val drawActions = Seq(DrawStartingHandsEvent(playersToDrawAndMulligan))
    val actions = if (mulligansSoFar < currentGameState.gameData.startingHandSize) {
      val mulliganChoices = playersToDrawAndMulligan.map(MulliganChoice(_, mulligansSoFar))
      val executeMulligansAction = ExecuteMulligansAction(mulligansSoFar)
      drawActions ++ mulliganChoices :+ executeMulligansAction
    } else {
      drawActions ++ playersToDrawAndMulligan.map(ReturnCardsToLibraryChoice(_, currentGameState.gameData.startingHandSize))
    }
    (actions, None)
  }
}
