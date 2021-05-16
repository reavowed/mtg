package mtg.web.visibleState

import mtg.game.{GameData, PlayerIdentifier}
import mtg.game.state.{Choice, GameState}

case class VisibleState(
  player: PlayerIdentifier,
  gameData: GameData,
  hand: Seq[VisibleGameObject],
  mulliganState: Map[PlayerIdentifier, MulliganState],
  currentChoice: Option[CurrentChoice])

object VisibleState {
  def forPlayer(playerIdentifier: PlayerIdentifier, gameState: GameState): VisibleState = {
    VisibleState(
      playerIdentifier,
      gameState.gameData,
      gameState.gameObjectState.hands(playerIdentifier).map(VisibleGameObject.apply),
      MulliganState.forAllPlayers(gameState),
      gameState.pendingActions.head.asOptionalInstanceOf[Choice].map(CurrentChoice(_)))
  }
}
