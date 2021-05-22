package mtg.web.visibleState

import mtg.game.objects.GameObject
import mtg.game.state.{Choice, GameState}
import mtg.game.turns.{TurnPhase, TurnStep}
import mtg.game.{GameData, PlayerIdentifier}
import mtg.parts.mana.ManaType

case class VisibleState(
  player: PlayerIdentifier,
  gameData: GameData,
  currentTurnNumber: Int,
  currentPhase: Option[TurnPhase],
  currentStep: Option[TurnStep],
  lifeTotals: Map[PlayerIdentifier, Int],
  hand: Seq[VisibleGameObject],
  battlefield: Map[PlayerIdentifier, Seq[VisibleGameObject]],
  stack: Seq[VisibleGameObject],
  manaPools: Map[PlayerIdentifier, Seq[ManaType]],
  mulliganState: Option[Map[PlayerIdentifier, MulliganState]],
  currentChoice: Option[CurrentChoice],
  log: Seq[LogEventWrapper])

object VisibleState {
  def forPlayer(playerIdentifier: PlayerIdentifier, gameState: GameState): VisibleState = {
    def getObject(gameObject: GameObject): VisibleGameObject = VisibleGameObject(gameObject, gameState.derivedState)
    VisibleState(
      playerIdentifier,
      gameState.gameData,
      gameState.currentTurnNumber,
      gameState.currentPhase,
      gameState.currentStep,
      gameState.gameObjectState.lifeTotals,
      gameState.gameObjectState.hands(playerIdentifier).map(getObject),
      gameState.gameObjectState.battlefield.groupBy(_.owner).view.mapValues(_.map(getObject)).toMap,
      gameState.gameObjectState.stack.map(getObject),
      gameState.gameObjectState.manaPools.view.mapValues(_.map(_.manaType)).toMap,
      MulliganState.forAllPlayers(gameState),
      gameState.pendingActions.head.asOptionalInstanceOf[Choice].map(CurrentChoice(_)),
      gameState.gameHistory.logEvents.map(LogEventWrapper.apply))
  }
}
