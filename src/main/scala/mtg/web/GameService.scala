package mtg.web

import mtg.data.cards.{Forest, Plains}
import mtg.data.cards.strixhaven.{AgelessGuardian, SpinedKarok}
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
  val gameStartingData = GameStartingData(Seq(
    PlayerStartingData(playerOne, (Seq.fill(30)(AgelessGuardian) ++ Seq.fill(30)(Plains)).map(Strixhaven.cardPrintingsByDefinition), Nil),
    PlayerStartingData(playerTwo, (Seq.fill(30)(SpinedKarok) ++ Seq.fill(30)(Forest)).map(Strixhaven.cardPrintingsByDefinition), Nil)))
  val gameStateManager = GameStateManager.initial(gameStartingData, onStateUpdate)
  gameStateManager.currentGameState.gameData.playersInTurnOrder.foreach(gameStateManager.handleDecision("K", _))

  def onStateUpdate(gameState: GameState): Unit = {
    players.foreach(player => {
      simpMessagingTemplate.convertAndSendToUser(player.id, "/topic/state", VisibleState.forPlayer(player, gameState))
    })
  }
}
