package mtg.game.start.mulligans

import mtg.game.PlayerIdentifier
import mtg.game.state.{GameAction, GameState, TypedChoice}

case class MulliganChoice(playerToAct: PlayerIdentifier, mulligansSoFar: Int)
  extends TypedChoice[MulliganOption] with TypedChoice.PartialFunctionParser[MulliganOption]
{
  override def optionParser(currentGameState: GameState): PartialFunction[String, MulliganOption] = {
    case "M" => MulliganOption.Mulligan
    case "K" => MulliganOption.Keep
  }

  override def handleDecision(chosenOption: MulliganOption, currentGameState: GameState): Seq[GameAction] = {
    if (chosenOption == MulliganOption.Keep && mulligansSoFar > 0)
      Seq(ReturnCardsToLibraryChoice(playerToAct, mulligansSoFar))
    else
      Nil
  }
}
