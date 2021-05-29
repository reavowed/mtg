package mtg.web.visibleState

import mtg.effects.SearchChoice
import mtg.game.actions.ResolveEffectChoice
import mtg.game.objects.GameObject
import mtg.game.state.{GameState, PlayerChoice}
import mtg.game.turns.{TurnPhase, TurnStep}
import mtg.game.{GameData, PlayerId, Zone}
import mtg.parts.mana.ManaType

case class VisibleState(
  player: PlayerId,
  gameData: GameData,
  currentTurnNumber: Int,
  currentPhase: Option[TurnPhase],
  currentStep: Option[TurnStep],
  lifeTotals: Map[PlayerId, Int],
  hands: Map[PlayerId, HiddenZoneContents],
  libraries: Map[PlayerId, HiddenZoneContents],
  battlefield: Map[PlayerId, Seq[VisibleGameObject]],
  stack: Seq[VisibleGameObject],
  manaPools: Map[PlayerId, Seq[ManaType]],
  mulliganState: Option[Map[PlayerId, MulliganState]],
  currentChoice: Option[CurrentChoice],
  log: Seq[LogEventWrapper]
)

object VisibleState {
  def forPlayer(playerIdentifier: PlayerId, gameState: GameState): VisibleState = {
    def getObject(gameObject: GameObject): VisibleGameObject = VisibleGameObject(gameObject, gameState)
    def currentChoice = gameState.pendingActions.head.asOptionalInstanceOf[PlayerChoice]
    def currentlySearchingZone = currentChoice.flatMap(_.asOptionalInstanceOf[ResolveEffectChoice])
      .flatMap(_.effectChoice.asOptionalInstanceOf[SearchChoice])
      .filter(_.playerChoosing == playerIdentifier)
      .map(_.zone)
    def getHiddenZoneContents(zone: Zone, contents: Seq[GameObject]): HiddenZoneContents = {
      if (zone == Zone.Hand(playerIdentifier) || currentlySearchingZone.contains(zone))
        HiddenZoneContents.CanSee(contents.map(getObject))
      else
        HiddenZoneContents.CantSee(contents.length)
    }
    VisibleState(
      playerIdentifier,
      gameState.gameData,
      gameState.currentTurnNumber,
      gameState.currentPhase,
      gameState.currentStep,
      gameState.gameObjectState.lifeTotals,
      gameState.gameObjectState.hands.map { case (player, contents) => player -> getHiddenZoneContents(Zone.Hand(player), contents) },
      gameState.gameObjectState.libraries.map { case (player, contents) => player -> getHiddenZoneContents(Zone.Library(player), contents) },
      gameState.gameObjectState.battlefield.view.map(getObject).toSeq.groupBy(_.controller.get),
      gameState.gameObjectState.stack.map(getObject),
      gameState.gameObjectState.manaPools.view.mapValues(_.map(_.manaType)).toMap,
      MulliganState.forAllPlayers(gameState),
      currentChoice.map(CurrentChoice(_)),
      gameState.gameHistory.logEvents.map(LogEventWrapper.apply))
  }
}
