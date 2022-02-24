package mtg.web.visibleState

import mtg.core.PlayerId
import mtg.core.zones.Zone
import mtg.game.GameData
import mtg.game.objects.{GameObject, ManaObject}
import mtg.game.state.{Choice, GameState, UndoHelper}
import mtg.game.turns.{TurnPhase, TurnStep}

case class VisibleState(
  player: PlayerId,
  gameData: GameData,
  currentTurnNumber: Int,
  currentPhase: Option[TurnPhase],
  currentStep: Option[TurnStep],
  lifeTotals: Map[PlayerId, Int],
  hands: Map[PlayerId, Seq[PossiblyHiddenGameObject]],
  libraries: Map[PlayerId, Seq[PossiblyHiddenGameObject]],
  battlefield: Map[PlayerId, Seq[VisibleGameObject]],
  graveyards: Map[PlayerId, Seq[VisibleGameObject]],
  stack: Seq[VisibleGameObject],
  manaPools: Map[PlayerId, Seq[ManaObject]],
  currentChoice: Option[CurrentChoice],
  canUndoLastChoice: Boolean,
  log: Seq[LogEventWrapper]
)

object VisibleState {
  def forPlayer(playerIdentifier: PlayerId, gameState: GameState): VisibleState = {
    def getObject(gameObject: GameObject): VisibleGameObject = VisibleGameObject(gameObject, gameState)
    def currentChoice = gameState.allCurrentActions.lastOption.flatMap(_.asOptionalInstanceOf[Choice[_]])
    def getHiddenZoneContents(zone: Zone, contents: Seq[GameObject]): Seq[PossiblyHiddenGameObject] = {
      val canSeeZone = zone == Zone.Hand(playerIdentifier) || currentChoice.exists(_.temporarilyVisibleZones.contains(zone))
      def canSeeObject(gameObject: GameObject): Boolean = canSeeZone || currentChoice.exists(_.temporarilyVisibleObjects.contains(gameObject.objectId))
      contents.map(gameObject => if (canSeeObject(gameObject)) getObject(gameObject) else HiddenGameObject)
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
      gameState.gameObjectState.graveyards.view.mapValues(_.map(getObject)).toMap,
      gameState.gameObjectState.stack.map(getObject),
      gameState.gameObjectState.manaPools,
      currentChoice.map(CurrentChoice(_, gameState)),
      UndoHelper.canUndo(playerIdentifier, gameState),
      gameState.gameHistory.logEvents.map(LogEventWrapper.apply))
  }
}
