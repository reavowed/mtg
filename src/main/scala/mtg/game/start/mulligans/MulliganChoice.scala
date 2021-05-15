package mtg.game.start.mulligans

import mtg.game.PlayerIdentifier
import mtg.game.state.{GameAction, GameState, TypedChoice}

case class MulliganChoice(playerToAct: PlayerIdentifier)
  extends TypedChoice[MulliganOption]
{
  override def parseOption: PartialFunction[String, MulliganOption] = {
    case "M" => MulliganOption.Mulligan
    case "K" => MulliganOption.Keep
  }

  override def handleDecision(chosenOption: MulliganOption, currentGameState: GameState): (GameState, Seq[GameAction]) = {
    (currentGameState, Nil)
  }
}
