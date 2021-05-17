package mtg.web.visibleState

import mtg.game.state.{Choice, GameState}
import mtg.game.{GameData, PlayerIdentifier}

case class VisibleState(
  player: PlayerIdentifier,
  gameData: GameData,
  hand: Seq[VisibleGameObject],
  mulliganState: Map[PlayerIdentifier, MulliganState],
  currentChoice: Option[CurrentChoice],
  log: Seq[LogEventWrapper])

object VisibleState {
  def forPlayer(playerIdentifier: PlayerIdentifier, gameState: GameState): VisibleState = {
    VisibleState(
      playerIdentifier,
      gameState.gameData,
      gameState.gameObjectState.hands(playerIdentifier).map(VisibleGameObject.apply),
      MulliganState.forAllPlayers(gameState),
      gameState.pendingActions.head.asOptionalInstanceOf[Choice].map(CurrentChoice(_)),
      gameState.gameHistory.allLogEvents.map(LogEventWrapper.apply))
  }
}
