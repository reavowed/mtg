package mtg.web

import mtg.data.cards.kaldheim.GrizzledOutrider
import mtg.data.cards.{Forest, Plains}
import mtg.data.cards.strixhaven.{AgelessGuardian, SpinedKarok}
import mtg.data.sets.Strixhaven
import mtg.game.objects.{Card, CardObject, ObjectId}
import mtg.game.{GameStartingData, PlayerIdentifier, PlayerStartingData, Zone}
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
  val initialManager = GameStateManager.initial(gameStartingData, _ => {})
  val initialGameState = initialManager.currentGameState
  val initialCards = Seq(AgelessGuardian, SpinedKarok, GrizzledOutrider).map(d => mtg.cards.Set.All.mapFind(_.cardPrintingsByDefinition.get(d)).get)
  val updatedState = initialGameState.updateGameObjectState(
    initialGameState.gameObjectState.copy(
      battlefield = initialCards.mapWithIndex((p, i) => CardObject(Card(playerOne, p), ObjectId(initialGameState.gameObjectState.nextObjectId + i), Zone.Battlefield, Some(playerOne), Zone.Battlefield.defaultPermanentStatus, 0)) ++
        initialCards.mapWithIndex((p, i) => CardObject(Card(playerTwo, p), ObjectId(initialGameState.gameObjectState.nextObjectId + 3 + i), Zone.Battlefield, Some(playerTwo), Zone.Battlefield.defaultPermanentStatus, 0)),
      nextObjectId = initialGameState.gameObjectState.nextObjectId + 6))

  val gameStateManager = new GameStateManager(updatedState, onStateUpdate, initialManager.stops)
  gameStateManager.currentGameState.gameData.playersInTurnOrder.foreach(gameStateManager.handleDecision("K", _))

  def onStateUpdate(gameState: GameState): Unit = {
    players.foreach(player => {
      simpMessagingTemplate.convertAndSendToUser(player.id, "/topic/state", VisibleState.forPlayer(player, gameState))
    })
  }
}
