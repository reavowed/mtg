package mtg.game

import mtg.game.`object`.{Card, CardObject, ObjectId}
import mtg.game.zone.{LibraryIdentifier, LibraryState, ZoneStates}

import scala.util.Random

object GameInitializer {
  def startGame(gameStartingData: GameStartingData): GameState = {
    val startingPlayer = Random.shuffle(gameStartingData.players).head
    val (libraries, nextObjectId) = initializeLibraries(gameStartingData)
    val sideboards = initializeSideboards(gameStartingData)
    GameState(nextObjectId, ZoneStates(libraries), sideboards)
  }

  private def initializeLibraries(gameStartingData: GameStartingData): (Map[PlayerIdentifier, LibraryState], Int) = {
    var nextObjectId = 1
    def getNextObjectId = {
      val objectId = ObjectId(nextObjectId)
      nextObjectId += 1
      objectId
    }
    var libraries = Map.empty[PlayerIdentifier, LibraryState]
    gameStartingData.players.foreach(playerStartingData => {
      import playerStartingData._
      val libraryIdentifier = new LibraryIdentifier(playerIdentifier)
      val shuffledDeck = Random.shuffle(deck)
      val cardObjects = shuffledDeck.map(card => new CardObject(Card(playerIdentifier, card), getNextObjectId, libraryIdentifier))
      libraries += (playerStartingData.playerIdentifier -> LibraryState(cardObjects))
    })
    (libraries, nextObjectId)
  }

  private def initializeSideboards(gameStartingData: GameStartingData): Map[PlayerIdentifier, Sideboard] = {
    gameStartingData.players.map(playerStartingData => {
      import playerStartingData._
      playerIdentifier -> Sideboard(sideboard.map(Card(playerIdentifier, _)))
    }).toMap
  }
}
