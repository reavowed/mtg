package mtg.game.start

import mtg.game.`object`.{Card, CardObject, ObjectId}
import mtg.game.loop.{Action, Choice, GameResult, ResolveEventAction}
import mtg.game.zone.{Library, ZoneState, ZoneStates}
import mtg.game.{GameStartingData, GameState, PlayerIdentifier, Sideboard}

import scala.util.Random

case class StartGameAction(gameStartingData: GameStartingData) extends Action {
  override def execute(): Either[Action, Either[Choice, GameResult]] = {
    val startingPlayer = Random.shuffle(gameStartingData.players).head
    val playersInTurnOrder = GameState.getPlayersInApNapOrder(startingPlayer, gameStartingData.players)
    val (libraries, nextObjectId) = initializeLibraries(gameStartingData)
    val sideboards = initializeSideboards(gameStartingData)
    val hands = initializeHands(gameStartingData)
    val initialState = GameState(nextObjectId, playersInTurnOrder, ZoneStates(libraries, hands), sideboards)
    Left(ResolveEventAction(InitialDrawEvent, initialState, MulliganChoice.initial))
  }

  private def initializeLibraries(gameStartingData: GameStartingData): (Map[PlayerIdentifier, ZoneState], Int) = {
    var nextObjectId = 1
    def getNextObjectId = {
      val objectId = ObjectId(nextObjectId)
      nextObjectId += 1
      objectId
    }
    var libraries = Map.empty[PlayerIdentifier, ZoneState]
    gameStartingData.playerData.foreach(playerStartingData => {
      import playerStartingData._
      val shuffledDeck = Random.shuffle(deck)
      val cardObjects = shuffledDeck.map(card => CardObject(Card(playerIdentifier, card), getNextObjectId, Library(playerIdentifier)))
      libraries += (playerStartingData.playerIdentifier -> ZoneState(cardObjects))
    })
    (libraries, nextObjectId)
  }

  private def initializeSideboards(gameStartingData: GameStartingData): Map[PlayerIdentifier, Sideboard] = {
    gameStartingData.playerData.map(playerStartingData => {
      import playerStartingData._
      playerIdentifier -> Sideboard(sideboard.map(Card(playerIdentifier, _)))
    }).toMap
  }

  private def initializeHands(gameStartingData: GameStartingData): Map[PlayerIdentifier, ZoneState] = {
    gameStartingData.playerData.map(playerStartingData => {
      playerStartingData.playerIdentifier -> ZoneState(Nil)
    }).toMap
  }
}
