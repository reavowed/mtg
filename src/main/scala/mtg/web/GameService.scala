package mtg.web

import mtg.cards.{CardDefinition, CardPrinting}
import mtg.core.PlayerId
import mtg.core.zones.Zone
import mtg.game.objects.{BasicGameObject, Card, PermanentObject}
import mtg.game.state.{GameState, GameStateManager}
import mtg.game.turns.turnEvents.ExecuteTurn
import mtg.game.{GameStartingData, PlayerStartingData}
import mtg.sets.alpha.cards.{LightningBolt, Mountain, Plains}
import mtg.sets.coreSet2021.cards.ConcordiaPegasus
import mtg.sets.kaldheim.cards.GrizzledOutrider
import mtg.sets.strixhaven.cards.{BeamingDefiance, EnvironmentalSciences}
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
      gameState.updateGameObjectState(_.addObjectToHand(player, BasicGameObject(Card(player, findCardPrinting(cardDefinition)), _, Zone.Hand(player))))
    }
    def addCardToBattlefield(player: PlayerId, cardDefinition: CardDefinition): GameState = {
      gameState.updateGameObjectState(_.addObjectToBattlefield(PermanentObject(Card(player, findCardPrinting(cardDefinition)), _, player)))
    }
  }

  val playerOne = PlayerId("P1")
  val playerTwo = PlayerId("P2")
  val players = Seq(playerOne, playerTwo)

  val gameStateManager: GameStateManager = {
    val gameStartingData = GameStartingData(Seq(
      PlayerStartingData(playerOne, (Seq.fill(30)(LightningBolt) ++ Seq.fill(30)(Mountain)).map(findCardPrinting), Nil),
      PlayerStartingData(playerTwo, (Seq.fill(30)(BeamingDefiance) ++ Seq.fill(30)(Plains)).map(findCardPrinting), Nil)))

    val initialManager = GameStateManager.initial(gameStartingData, _ => {})
    val initialGameState = initialManager.gameState
    val updatedGameState = initialGameState
      .addCardToHand(playerOne, EnvironmentalSciences)
      .addCardToBattlefield(playerOne, Plains)
      .addCardToBattlefield(playerOne, Plains)
      .addCardToBattlefield(playerOne, Plains)
      .addCardToBattlefield(playerOne, Plains)
      .addCardToBattlefield(playerOne, ConcordiaPegasus)
      .addCardToBattlefield(playerTwo, Plains)
      .addCardToBattlefield(playerTwo, Plains)
      .addCardToBattlefield(playerTwo, ConcordiaPegasus)
      .addCardToBattlefield(playerTwo, GrizzledOutrider)
      .copy(currentAction = Some(ExecuteTurn.first(initialGameState)))

    new GameStateManager(updatedGameState, onStateUpdate, initialManager.stops)
  }

  def onStateUpdate(gameState: GameState): Unit = {
    players.foreach(player => {
      simpMessagingTemplate.convertAndSendToUser(player.toString, "/topic/state", VisibleState.forPlayer(player, gameState))
    })
  }
}
