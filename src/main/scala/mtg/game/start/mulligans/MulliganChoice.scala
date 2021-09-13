package mtg.game.start.mulligans

import mtg.game.PlayerId
import mtg.game.state.history.LogEvent
import mtg.game.state.{GameState, InternalGameActionResult, TypedPlayerChoice}

case class MulliganChoice(playerToAct: PlayerId, mulligansSoFar: Int)
  extends TypedPlayerChoice[MulliganOption] with TypedPlayerChoice.PartialFunctionParser[MulliganOption]
{
  override def optionParser(currentGameState: GameState): PartialFunction[String, MulliganOption] = {
    case "M" => MulliganOption.Mulligan
    case "K" => MulliganOption.Keep
  }

  override def handleDecision(chosenOption: MulliganOption, currentGameState: GameState): InternalGameActionResult = {
    val actions = if (chosenOption == MulliganOption.Keep && mulligansSoFar > 0)
      Seq(ReturnCardsToLibraryChoice(playerToAct, mulligansSoFar))
    else
      Nil
    val logEvent = chosenOption match {
      case MulliganOption.Mulligan => LogEvent.Mulligan(playerToAct, currentGameState.gameData.startingHandSize - mulligansSoFar - 1)
      case MulliganOption.Keep => LogEvent.KeepHand(playerToAct, currentGameState.gameData.startingHandSize - mulligansSoFar)
    }
    (actions, logEvent)
  }
}
