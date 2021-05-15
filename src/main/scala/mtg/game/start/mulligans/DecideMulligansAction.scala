package mtg.game.start.mulligans

import mtg.game.PlayerIdentifier
import mtg.game.start.StartFirstTurnAction
import mtg.game.state.{AutomaticGameAction, GameState}

case class DecideMulligansAction(playersToMulligan: Seq[PlayerIdentifier], mulligansSoFar: Int) extends AutomaticGameAction {
  override def execute(currentGameState: GameState): GameState = {
    val transition = playersToMulligan match {
      case playerToAct +: playersWaitingToAct =>
        MulliganChoice(playerToAct, playersWaitingToAct, Nil, mulligansSoFar)
      case Nil =>
        StartFirstTurnAction
    }
    currentGameState.updateTransition(transition)
  }
}
