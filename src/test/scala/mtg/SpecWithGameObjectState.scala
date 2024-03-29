package mtg

import mtg.definitions.PlayerId
import mtg.definitions.zones.Zone
import mtg.game._
import mtg.game.objects._
import mtg.game.state.GameState
import mtg.helpers.{GameObjectHelpers, GameObjectStateHelpers}
import mtg.sets.strixhaven.Strixhaven
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
      .setHand(playerOne, playerOneInitialHand: _*)
      .setLibrary(playerOne, playerOneInitialLibrary: _*)
      .setHand(playerTwo, playerTwoInitialHand: _*)
      .setLibrary(playerTwo, playerTwoInitialLibrary: _*)
  }

  val gameObjectStateWithInitialLibrariesOnly = emptyGameObjectState
    .setLibrary(playerOne, playerOneAllCards: _*)
    .setLibrary(playerTwo, playerTwoAllCards: _*)
  val gameObjectStateWithInitialLibrariesAndHands = setInitialHandAndLibrary(emptyGameObjectState)

  implicit class PlayerOps(playerIdentifier: PlayerId) {
    def library = Zone.Library(playerIdentifier)
    def hand = Zone.Hand(playerIdentifier)
    def graveyard = Zone.Graveyard(playerIdentifier)

    def lifeTotal(gameState: GameState): Int = lifeTotal(gameState.gameObjectState)
    def lifeTotal(gameObjectState: GameObjectState): Int = gameObjectState.lifeTotals(playerIdentifier)
  }
  implicit class ZoneOps(zone: Zone) {
    def apply(gameState: GameState): Seq[GameObject] = {
      apply(gameState.gameObjectState)
    }
    def apply(gameObjectState: GameObjectState): Seq[GameObject] = {
      gameObjectState.getZoneState(zone)
    }
  }
}
