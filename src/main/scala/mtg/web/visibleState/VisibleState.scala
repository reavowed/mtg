package mtg.web.visibleState

import mtg.game.state.{Choice, GameState}
import mtg.game.turns.{TurnPhase, TurnStep}
import mtg.game.{GameData, PlayerIdentifier}

case class VisibleState(
  player: PlayerIdentifier,
  gameData: GameData,
  currentTurnNumber: Int,
  currentPhase: Option[TurnPhase],
  currentStep: Option[TurnStep],
  hand: Seq[VisibleGameObject],
  battlefield: Map[PlayerIdentifier, Seq[VisibleGameObject]],
  mulliganState: Map[PlayerIdentifier, MulliganState],
  currentChoice: Option[CurrentChoice],
  log: Seq[LogEventWrapper])

object VisibleState {
  def forPlayer(playerIdentifier: PlayerIdentifier, gameState: GameState): VisibleState = {
    VisibleState(
      playerIdentifier,
      gameState.gameData,
      gameState.currentTurnNumber,
      gameState.currentPhase,
      gameState.currentStep,
      gameState.gameObjectState.hands(playerIdentifier).map(VisibleGameObject.apply),
      gameState.gameObjectState.battlefield.groupBy(_.owner).view.mapValues(_.map(VisibleGameObject.apply)).toMap,
      MulliganState.forAllPlayers(gameState),
      gameState.pendingActions.head.asOptionalInstanceOf[Choice].map(CurrentChoice(_)),
      gameState.gameHistory.logEvents.map(LogEventWrapper.apply))
  }
}
