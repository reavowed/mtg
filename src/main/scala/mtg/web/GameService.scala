package mtg.web

import mtg.cards.{CardDefinition, CardPrinting}
import mtg.core.PlayerId
import mtg.core.zones.Zone
import mtg.game.objects.{BasicGameObject, Card, PermanentObject}
import mtg.game.state.{GameState, GameStateManager}
import mtg.game.turns.turnEvents.ExecuteTurn
import mtg.game.{GameStartingData, PlayerStartingData}
import mtg.sets.alpha.cards.{LightningBolt, Plains}
import mtg.sets.coreSet2021.cards.ConcordiaPegasus
import mtg.sets.kaldheim.cards.GrizzledOutrider
import mtg.sets.strixhaven.Strixhaven
import mtg.sets.strixhaven.cards.{EnvironmentalSciences, GuidingVoice, IntroductionToProphecy, PilgrimOfTheAges, PillardropRescuer}
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
      .addCardToGraveyard(playerOne, PilgrimOfTheAges)
      .addCardToGraveyard(playerOne, EnvironmentalSciences)
      .addCardToGraveyard(playerOne, PillardropRescuer)
      .addCardToSideboard(playerOne, EnvironmentalSciences)
      .addCardToSideboard(playerOne, IntroductionToProphecy)
      .addCardToSideboard(playerOne, LightningBolt)
      .addCardToSideboard(playerOne, Plains)
      .addCardToBattlefield(playerOne, Plains)
      .addCardToBattlefield(playerOne, Plains)
      .addCardToBattlefield(playerOne, Plains)
      .addCardToBattlefield(playerOne, Plains)
      .addCardToBattlefield(playerOne, Plains)
      .addCardToBattlefield(playerOne, Plains)
      .addCardToBattlefield(playerTwo, Plains)
      .addCardToBattlefield(playerTwo, Plains)
      .addCardToHand(playerOne, PillardropRescuer)
      .copy(currentAction = Some(ExecuteTurn.first(initialGameState)))

    new GameStateManager(updatedGameState, onStateUpdate, initialManager.stops)
  }

  def onStateUpdate(gameState: GameState): Unit = {
    players.foreach(player => {
      simpMessagingTemplate.convertAndSendToUser(player.toString, "/topic/state", VisibleState.forPlayer(player, gameState))
    })
  }
}
