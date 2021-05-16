package mtg.web

import mtg.data.sets.Strixhaven
import mtg.game.{GameStartingData, PlayerIdentifier, PlayerStartingData}
import mtg.game.state.{GameState, GameStateManager}
import mtg.web.visibleState.VisibleState
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Service

@Service
class GameService @Autowired() (simpMessagingTemplate: SimpMessagingTemplate) {
  val playerOne = PlayerIdentifier("P1")
  val playerTwo = PlayerIdentifier("P2")
  val players = Seq(playerOne, playerTwo)
  val gameStartingData = GameStartingData(players.map(PlayerStartingData(_, Strixhaven.cardPrintings, Nil)))
  val gameStateManager = GameStateManager.initial(gameStartingData, onStateUpdate)

  def onStateUpdate(gameState: GameState): Unit = {
    players.foreach(player => {
      simpMessagingTemplate.convertAndSendToUser(player.id, "/topic/state", VisibleState.forPlayer(player, gameState))
    })
  }
}
