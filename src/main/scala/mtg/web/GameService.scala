package mtg.web

import mtg.cards.{CardDefinition, CardPrinting}
import mtg.definitions.PlayerId
import mtg.definitions.zones.Zone
import mtg.game.objects.{BasicGameObject, Card, PermanentObject}
import mtg.game.state.{GameActionExecutionState, GameState, GameStateManager}
import mtg.game.turns.turnEvents.ExecuteTurn
import mtg.game.{GameStartingData, PlayerStartingData}
import mtg.sets.alpha.cards.{LightningBolt, Mountain, Plains}
import mtg.sets.strixhaven.Strixhaven
import mtg.sets.strixhaven.cards.{ExpandedAnatomy, SpinedKarok, StarPupil}
import mtg.web.visibleState.VisibleState
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Service

@Service
class GameService @Autowired() (simpMessagingTemplate: SimpMessagingTemplate) {
  def findCardPrinting(cardDefinition: CardDefinition): CardPrinting = {
    mtg.cards.Set.All.mapFind(_.cardPrintings.find(_.cardDefinition == cardDefinition)).get
  }

  implicit class GameStateExtensions(gameState: GameState) {
    def addCardToHand(player: PlayerId, cardDefinition: CardDefinition): GameState = {
      gameState.updateGameObjectState(_.addObjectToHand(player, BasicGameObject(Card(player, findCardPrinting(cardDefinition)), _, Zone.Hand(player)))._2)
    }
    def addCardToBattlefield(player: PlayerId, cardDefinition: CardDefinition): GameState = {
      gameState.updateGameObjectState(_.addObjectToBattlefield(PermanentObject(Card(player, findCardPrinting(cardDefinition)), _, player))._2)
    }
    def addCardToSideboard(player: PlayerId, cardDefinition: CardDefinition): GameState = {
      gameState.updateGameObjectState(_.addObjectToSideboard(player, BasicGameObject(Card(player, findCardPrinting(cardDefinition)), _, Zone.Sideboard(player)))._2)
    }
    def addCardToGraveyard(player: PlayerId, cardDefinition: CardDefinition): GameState = {
      gameState.updateGameObjectState(_.addObjectToGraveyard(player, BasicGameObject(Card(player, findCardPrinting(cardDefinition)), _, Zone.Graveyard(player)))._2)
    }
  }

  val playerOne = PlayerId("P1")
  val playerTwo = PlayerId("P2")
  val players = Seq(playerOne, playerTwo)

  val gameStateManager: GameStateManager = {
    val gameStartingData = GameStartingData(Seq(
      PlayerStartingData(playerOne, Strixhaven.cardPrintings, Nil),
      PlayerStartingData(playerTwo, Strixhaven.cardPrintings, Nil)))

    val initialManager = GameStateManager.initial(gameStartingData, _ => {})
    val initialGameState = initialManager.gameState
    val updatedGameState = initialGameState
      .addCardToHand(playerOne, StarPupil)
      .addCardToHand(playerOne, ExpandedAnatomy)
      .addCardToBattlefield(playerOne, SpinedKarok)
      .addCardToBattlefield(playerOne, Plains)
      .addCardToBattlefield(playerOne, Plains)
      .addCardToBattlefield(playerOne, Plains)
      .addCardToHand(playerTwo, LightningBolt)
      .addCardToBattlefield(playerTwo, Mountain)
      .copy(currentActionExecutionState = GameActionExecutionState.Action(ExecuteTurn.first(initialGameState)))

    new GameStateManager(updatedGameState, onStateUpdate, initialManager.stops)
  }

  def onStateUpdate(gameState: GameState): Unit = {
    players.foreach(player => {
      simpMessagingTemplate.convertAndSendToUser(player.toString, "/topic/state", VisibleState.forPlayer(player, gameState))
    })
  }
}
