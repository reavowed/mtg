package mtg.web

import mtg.cards.CardDefinition
import mtg.data.cards.strixhaven.EnvironmentalSciences
import mtg.data.cards.{Forest, Plains}
import mtg.data.sets.Strixhaven
import mtg.game.objects.{Card, CardObject}
import mtg.game.state.{GameState, GameStateManager}
import mtg.game.{GameStartingData, PlayerIdentifier, PlayerStartingData, Zone}
import mtg.web.visibleState.VisibleState
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Service

@Service
class GameService @Autowired() (simpMessagingTemplate: SimpMessagingTemplate) {
  def addCard(gameState: GameState, cardDefinition: CardDefinition, zone: Zone, owner: PlayerIdentifier): GameState = {
    val printing = mtg.cards.Set.All.mapFind(_.cardPrintings.find(_.cardDefinition == cardDefinition)).get
    gameState.updateGameObjectState(gameState.gameObjectState.addObject(zone, CardObject(Card(playerOne, printing), _, zone, Some(owner), zone.defaultPermanentStatus, 0), _.length))
  }
  val playerOne = PlayerIdentifier("P1")
  val playerTwo = PlayerIdentifier("P2")
  val players = Seq(playerOne, playerTwo)

  val gameStateManager: GameStateManager = {
    val gameStartingData = GameStartingData(Seq(
      PlayerStartingData(playerOne, (Seq.fill(30)(EnvironmentalSciences) ++ Seq.fill(30)(Plains)).map(Strixhaven.cardPrintingsByDefinition), Nil),
      PlayerStartingData(playerTwo, (Seq.fill(30)(EnvironmentalSciences) ++ Seq.fill(30)(Forest)).map(Strixhaven.cardPrintingsByDefinition), Nil)))

    val initialManager = GameStateManager.initial(gameStartingData, _ => {})
    val initialGameState = initialManager.currentGameState

    val cardsToAdd = Seq(
      (Forest, Zone.Battlefield, playerOne),
      (Forest, Zone.Battlefield, playerOne)
    )

    val updatedState = cardsToAdd.foldLeft(initialGameState) { case (state, (cardDefinition, zone, player)) => addCard(state, cardDefinition, zone, player)}
    new GameStateManager(updatedState, onStateUpdate, initialManager.stops)
  }

  def onStateUpdate(gameState: GameState): Unit = {
    players.foreach(player => {
      simpMessagingTemplate.convertAndSendToUser(player.id, "/topic/state", VisibleState.forPlayer(player, gameState))
    })
  }
}
