package mtg

import mtg.data.sets.Strixhaven
import mtg.game.objects._
import mtg.game.state.{GameAction, GameState}
import mtg.game.state.history.GameHistory
import mtg.game.{GameData, GameStartingData, PlayerId, PlayerStartingData, Zone}
import mtg.helpers.{GameObjectHelpers, GameObjectStateHelpers}
import org.specs2.mutable.SpecificationLike

trait SpecWithGameObjectState
    extends SpecificationLike
    with GameObjectStateHelpers
    with GameObjectHelpers
{
  val playerOne = PlayerId("P1")
  val playerTwo = PlayerId("P2")
  val players = Seq(playerOne, playerTwo)
  val gameData = GameData.initial(players)

  val playerOneAllCards = Strixhaven.cardPrintings.map(_.cardDefinition)
  val playerTwoAllCards = playerOneAllCards.reverse

  val (playerOneInitialHand, playerOneInitialLibrary) = playerOneAllCards.splitAt(7)
  val (playerTwoInitialHand, playerTwoInitialLibrary) = playerTwoAllCards.splitAt(7)

  val emptyGameObjectState = GameObjectState.initial(GameStartingData(players.map(PlayerStartingData(_, Nil, Nil))), gameData)

  def setInitialHandAndLibrary(gameObjectState: GameObjectState): GameObjectState = {
    gameObjectState
      .setHand(playerOne, playerOneInitialHand)
      .setLibrary(playerOne, playerOneInitialLibrary)
      .setHand(playerTwo, playerTwoInitialHand)
      .setLibrary(playerTwo, playerTwoInitialLibrary)
  }

  val gameObjectStateWithInitialLibrariesOnly = emptyGameObjectState.setLibrary(playerOne, playerOneAllCards).setLibrary(playerTwo, playerTwoAllCards)
  val gameObjectStateWithInitialLibrariesAndHands = setInitialHandAndLibrary(emptyGameObjectState)

  def createGameState(gameObjectState: GameObjectState, actions: Seq[GameAction]): GameState = {
    GameState(gameData, gameObjectState, GameHistory.empty, actions)
  }

  implicit class PlayerOps(playerIdentifier: PlayerId) {
    def library = Zone.Library(playerIdentifier)
    def hand = Zone.Hand(playerIdentifier)
    def graveyard = Zone.Graveyard(playerIdentifier)
  }
  implicit class ZoneOps(zone: Zone) {
    def apply(gameState: GameState): Seq[GameObject] = {
      apply(gameState.gameObjectState)
    }
    def apply(gameObjectState: GameObjectState): Seq[GameObject] = {
      zone.getState(gameObjectState)
    }
  }
}
