package mtg.game.start.mulligans

import mtg.game.PlayerIdentifier
import mtg.game.state.GameEvent.Decision
import mtg.game.state.{GameState, TypedChoice}

case class MulliganChoice(playerToAct: PlayerIdentifier, playersWaitingToAct: Seq[PlayerIdentifier], mulliganDecisions: Seq[Decision[MulliganOption]], mulligansSoFar: Int)
  extends TypedChoice[MulliganOption]
{
  override def parseOption: PartialFunction[String, MulliganOption] = {
    case "M" => MulliganOption.Mulligan
    case "K" => MulliganOption.Keep
  }

  override def handleDecision(chosenOption: MulliganOption, currentGameState: GameState): GameState = {
    val newMulliganDecisions = mulliganDecisions :+ Decision(chosenOption, playerToAct)
    val nextAction = playersWaitingToAct match {
      case playerToAct +: playersWaitingToAct =>
        MulliganChoice(playerToAct, playersWaitingToAct, newMulliganDecisions, mulligansSoFar)
      case Nil =>
        ExecuteMulligansAction(newMulliganDecisions, mulligansSoFar)
    }
    currentGameState.updateAction(nextAction)
  }
}
