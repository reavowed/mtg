package mtg.web.visibleState

import mtg.game.PlayerIdentifier
import mtg.game.start.mulligans.MulliganOption
import mtg.game.state.GameEvent.Decision
import mtg.game.state.GameState

case class MulliganState(hasKept: Boolean, mulligansTaken: Int)
object MulliganState {
  def forPlayer(playerIdentifier: PlayerIdentifier, gameState: GameState): MulliganState = {
    val decisions = gameState.gameHistory.preGame.gameEvents.ofType[Decision]
      .filter(_.playerIdentifier == playerIdentifier)
      .map(_.chosenOption)
      .ofType[MulliganOption]
    val mulligansTaken = decisions.count(_ == MulliganOption.Mulligan)
    val hasKept = decisions.contains(MulliganOption.Keep) || mulligansTaken == gameState.gameData.startingHandSize;
    MulliganState(hasKept, mulligansTaken)
  }
  def forAllPlayers(gameState: GameState): Map[PlayerIdentifier, MulliganState] = {
    gameState.gameData.playersInTurnOrder.map(p => p -> forPlayer(p, gameState)).toMap
  }
}