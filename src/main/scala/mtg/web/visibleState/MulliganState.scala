package mtg.web.visibleState

import mtg.game.PlayerIdentifier
import mtg.game.start.mulligans.MulliganOption
import mtg.game.state.GameEvent.Decision
import mtg.game.state.GameState

case class MulliganState(hasKept: Boolean, mulligansTaken: Int)
object MulliganState {
  def forPlayer(playerIdentifier: PlayerIdentifier, gameState: GameState): MulliganState = {
    val decisions = gameState.gameHistory.preGameEvents.ofType[Decision]
      .filter(_.playerIdentifier == playerIdentifier)
      .map(_.chosenOption)
      .ofType[MulliganOption];
    MulliganState(
      decisions.contains(MulliganOption.Keep),
      decisions.count(_ == MulliganOption.Mulligan))
  }
  def forAllPlayers(gameState: GameState): Map[String, MulliganState] = {
    gameState.gameData.playersInTurnOrder.map(p => p.id -> forPlayer(p, gameState)).toMap
  }
}
