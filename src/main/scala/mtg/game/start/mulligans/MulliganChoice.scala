package mtg.game.start.mulligans

import mtg.game.PlayerIdentifier
import mtg.game.state.history.LogEvent
import mtg.game.state.{GameAction, GameState, TypedChoice}

case class MulliganChoice(playerToAct: PlayerIdentifier, mulligansSoFar: Int)
  extends TypedChoice[MulliganOption] with TypedChoice.PartialFunctionParser[MulliganOption]
{
  override def optionParser(currentGameState: GameState): PartialFunction[String, MulliganOption] = {
    case "M" => MulliganOption.Mulligan
    case "K" => MulliganOption.Keep
  }

  override def handleDecision(chosenOption: MulliganOption, currentGameState: GameState): (Seq[GameAction], Option[LogEvent]) = {
    val actions = if (chosenOption == MulliganOption.Keep && mulligansSoFar > 0)
      Seq(ReturnCardsToLibraryChoice(playerToAct, mulligansSoFar))
    else
      Nil
    val logEvent = chosenOption match {
      case MulliganOption.Mulligan => LogEvent.Mulligan(playerToAct, currentGameState.gameData.startingHandSize - mulligansSoFar - 1)
      case MulliganOption.Keep => LogEvent.KeepHand(playerToAct, currentGameState.gameData.startingHandSize - mulligansSoFar)
    }
    (actions, Some(logEvent))
  }
}
