package mtg.web.visibleState

import mtg.game.PlayerIdentifier
import mtg.game.state.{Choice, GameState}

case class VisibleState(
  player: String,
  playersInTurnOrder: Seq[String],
  hand: Seq[VisibleGameObject],
  mulliganState: Map[String, MulliganState],
  currentChoice: Option[CurrentChoice])

object VisibleState {
  def forPlayer(playerIdentifier: PlayerIdentifier, gameState: GameState): VisibleState = {
    VisibleState(
      playerIdentifier.id,
      gameState.gameData.playersInTurnOrder.map(_.id),
      gameState.gameObjectState.hands(playerIdentifier).map(VisibleGameObject.apply),
      MulliganState.forAllPlayers(gameState),
      gameState.pendingActions.head.asOptionalInstanceOf[Choice].map(CurrentChoice(_)))
  }
}
