package mtg.web

import mtg.data.cards.Plains
import mtg.data.cards.strixhaven.AgelessGuardian
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
  val deckLol = (Seq.fill(38)(AgelessGuardian) ++ Seq.fill(22)(Plains)).map(Strixhaven.cardPrintingsByDefinition)
  val gameStartingData = GameStartingData(players.map(PlayerStartingData(_, deckLol, Nil)))
  val gameStateManager = GameStateManager.initial(gameStartingData, onStateUpdate)
  gameStateManager.currentGameState.gameData.playersInTurnOrder.foreach(gameStateManager.handleDecision("K", _))
  gameStateManager.currentGameState.playersInApnapOrder.foreach(gameStateManager.handleDecision("Pass", _))

  def onStateUpdate(gameState: GameState): Unit = {
    players.foreach(player => {
      simpMessagingTemplate.convertAndSendToUser(player.id, "/topic/state", VisibleState.forPlayer(player, gameState))
    })
  }
}
