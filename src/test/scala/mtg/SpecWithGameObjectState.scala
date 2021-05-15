package mtg

import mtg.cards.CardPrinting
import mtg.events.Event
import mtg.game.objects.{Card, CardObject, GameObject, GameObjectState, ObjectId}
import mtg.game.state._
import mtg.game.{GameData, PlayerIdentifier, Zone}
import org.specs2.mutable.Specification

abstract class SpecWithGameObjectState extends Specification {

  def emptyGameObjectState(players: Seq[PlayerIdentifier]) = GameObjectState(
    1,
    players.map(p => p -> Nil).toMap,
    players.map(p => p -> Nil).toMap,
    players.map(p => p -> Nil).toMap)

  implicit class GameObjectStateOps(gameObjectState: GameObjectState) {
    private def setZone(zone: Zone, playerIdentifier: PlayerIdentifier, cardPrintings: Seq[CardPrinting]): GameObjectState = {
      val cards = cardPrintings.map(Card(playerIdentifier, _))
      cards.foldLeft(gameObjectState.updateZone(zone, _ => Nil)) { (state, card) =>
        zone.stateLens.modify(s => s :+ CardObject(card, ObjectId(gameObjectState.nextObjectId), zone))(state).copy(nextObjectId = state.nextObjectId + 1)
      }
    }

    def setLibrary(playerIdentifier: PlayerIdentifier, cardPrintings: Seq[CardPrinting]): GameObjectState = {
      setZone(Zone.Library(playerIdentifier), playerIdentifier, cardPrintings)
    }
    def setHand(playerIdentifier: PlayerIdentifier, cardPrintings: Seq[CardPrinting]): GameObjectState = {
      setZone(Zone.Hand(playerIdentifier), playerIdentifier, cardPrintings)
    }
  }
  implicit class GameObjectOps(gameObject: GameObject) {
    def card: Card = gameObject.asInstanceOf[CardObject].card
  }
  implicit class GameStateManagerOps(gameStateManager: GameStateManager) {
    def updateGameState(f: GameState => GameState): GameStateManager = {
      new GameStateManager(f(gameStateManager.gameState))
    }
    def updateGameObjectState(f: GameObjectState => GameObjectState): GameStateManager = {
      updateGameState(_.updateGameObjectState(f(gameStateManager.gameState.gameObjectState)))
    }
  }

  def createGameStateManager(gameData: GameData, gameObjectState: GameObjectState, transition: Transition): GameStateManager = {
    val gameStateManager = new GameStateManager(GameState(gameData, gameObjectState, GameHistory.empty, transition))
    gameStateManager.initialize()
    gameStateManager
  }

  def runAction(action: AutomaticGameAction, gameData: GameData, gameObjectState: GameObjectState): GameState = {
    createGameStateManager(gameData, gameObjectState, action).gameState
  }

  def runEvent(event: Event, gameData: GameData, gameObjectState: GameObjectState): GameState = {
    runAction(HandleEventsAction(Seq(event), GameResult.Tie), gameData, gameObjectState)
  }
}
