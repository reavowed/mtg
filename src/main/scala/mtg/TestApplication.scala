package mtg

import mtg.data.cards.{Forest, Plains}
import mtg.data.sets.Strixhaven
import mtg.game.loop.GameLoop
import mtg.game.start.StartGameAction
import mtg.game.{GameStartingData, PlayerIdentifier, PlayerStartingData}

object TestApplication {
  def main(args: Array[String]): Unit = {
    val playerOne = PlayerStartingData(PlayerIdentifier("P1"), Seq.fill(60)(Strixhaven.getCard(Plains).get), Nil)
    val playerTwo = PlayerStartingData(PlayerIdentifier("P2"), Seq.fill(60)(Strixhaven.getCard(Forest).get), Nil)
    val gameStartingData = GameStartingData(Seq(playerOne, playerTwo))
    val result = GameLoop.executeAction(StartGameAction(gameStartingData))
    println(result)
  }
}
