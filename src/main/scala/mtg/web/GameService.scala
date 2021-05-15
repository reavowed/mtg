package mtg.web

import mtg.data.sets.Strixhaven
import mtg.game.{GameStartingData, PlayerIdentifier, PlayerStartingData}
import mtg.game.state.GameStateManager
import org.springframework.stereotype.Service

@Service
class GameService {
  val playerOne = PlayerIdentifier("P1")
  val playerTwo = PlayerIdentifier("P2")
  val players = Seq(playerOne, playerTwo)
  val gameStartingData = GameStartingData(players.map(PlayerStartingData(_, Strixhaven.cardPrintings, Nil)))
  val gameStateManager = GameStateManager.initial(gameStartingData)
}
