package mtg.web.visibleState

import mtg._
import mtg.game.PlayerId
import mtg.game.start.mulligans.MulliganOption
import mtg.game.state.GameState
import mtg.game.state.history.GameEvent.Decision

case class MulliganState(hasKept: Boolean, mulligansTaken: Int)
object MulliganState {
  def forPlayer(playerIdentifier: PlayerId, gameState: GameState): MulliganState = {
    val decisions = gameState.eventsThisTurn.ofType[Decision]
      .filter(_.playerIdentifier == playerIdentifier)
      .map(_.chosenOption)
      .ofType[MulliganOption]
    val mulligansTaken = decisions.count(_ == MulliganOption.Mulligan)
    val hasKept = decisions.contains(MulliganOption.Keep) || mulligansTaken == gameState.gameData.startingHandSize;
    MulliganState(hasKept, mulligansTaken)
  }
  def forAllPlayers(gameState: GameState): Option[Map[PlayerId, MulliganState]] = {
    if (gameState.turnState.currentTurnNumber == 0)
      Some(gameState.gameData.playersInTurnOrder.map(p => p -> forPlayer(p, gameState)).toMap)
    else
      None
  }
}
