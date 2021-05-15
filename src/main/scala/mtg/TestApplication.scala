package mtg

import mtg.data.cards.{Forest, Plains}
import mtg.data.sets.Strixhaven
import mtg.game.state.GameStateManager
import mtg.game.{GameStartingData, PlayerIdentifier, PlayerStartingData}

object TestApplication {
  def main(args: Array[String]): Unit = {
    val playerOne = PlayerStartingData(PlayerIdentifier("P1"), Seq.fill(60)(Strixhaven.getCard(Plains).get), Nil)
    val playerTwo = PlayerStartingData(PlayerIdentifier("P2"), Seq.fill(60)(Strixhaven.getCard(Forest).get), Nil)
    val gameStartingData = GameStartingData(Seq(playerOne, playerTwo))
    val gameStateManager = GameStateManager.initial(gameStartingData)
    println(gameStateManager.gameState)
  }
}
