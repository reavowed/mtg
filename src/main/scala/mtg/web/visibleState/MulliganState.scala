package mtg.web.visibleState

import mtg._
import mtg.game.PlayerId
import mtg.game.start.mulligans.MulliganDecision
import mtg.game.state.GameState

case class MulliganState(hasKept: Boolean, mulligansTaken: Int)
object MulliganState {
  def forPlayer(playerIdentifier: PlayerId, gameState: GameState): MulliganState = {
    val decisions = gameState.gameHistory.gameEventsThisTurn.actions.ofType[MulliganDecision]
      .filter(_.player == playerIdentifier)
      .toSeq.reverse
    val mulligansTaken = decisions.ofType[MulliganDecision.Mulligan].size
    val hasKept = decisions.exists(_.isInstanceOf[MulliganDecision.Keep]) || mulligansTaken == gameState.gameData.startingHandSize;
    MulliganState(hasKept, mulligansTaken)
  }
  def forAllPlayers(gameState: GameState): Option[Map[PlayerId, MulliganState]] = {
    if (gameState.currentTurnNumber == 0)
      Some(gameState.gameData.playersInTurnOrder.map(p => p -> forPlayer(p, gameState)).toMap)
    else
      None
  }
}
