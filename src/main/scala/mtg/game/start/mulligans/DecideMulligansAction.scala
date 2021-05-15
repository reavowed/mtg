package mtg.game.start.mulligans

import mtg.game.PlayerIdentifier
import mtg.game.start.StartFirstTurnAction
import mtg.game.state.{Action, GameState}

case class DecideMulligansAction(playersToMulligan: Seq[PlayerIdentifier], mulligansSoFar: Int) extends Action {
  override def runAction(currentGameState: GameState): GameState = {
    val transition = playersToMulligan match {
      case playerToAct +: playersWaitingToAct =>
        MulliganChoice(playerToAct, playersWaitingToAct, Nil, mulligansSoFar)
      case Nil =>
        StartFirstTurnAction
    }
    currentGameState.updateTransition(transition)
  }
}
