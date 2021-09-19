package mtg.web

import mtg.cards.{CardDefinition, CardPrinting}
import mtg.data.cards.alpha.LightningBolt
import mtg.data.cards.kaldheim.GrizzledOutrider
import mtg.data.cards.m21.ConcordiaPegasus
import mtg.data.cards.strixhaven.{BeamingDefiance, DefendTheCampus}
import mtg.data.cards.{Mountain, Plains}
import mtg.game.Zone.BasicZone
import mtg.game.objects.{BasicGameObject, Card, PermanentObject}
import mtg.game.state.{GameState, GameStateManager}
import mtg.game.turns.StartNextTurnAction
import mtg.game.{GameStartingData, PlayerId, PlayerStartingData, Zone}
import mtg.web.visibleState.VisibleState
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Service

@Service
class GameService @Autowired() (simpMessagingTemplate: SimpMessagingTemplate) {

  def findCard(cardDefinition: CardDefinition): CardPrinting = {
    mtg.cards.Set.All.mapFind(_.cardPrintings.find(_.cardDefinition == cardDefinition)).get
  }
  def addCard(gameState: GameState, cardDefinition: CardDefinition, zone: Zone, owner: PlayerId): GameState = {
    val card = Card(owner, findCard(cardDefinition))
    zone match {
      case Zone.Stack =>
        throw new Exception("Trying to create things directly on the stack seems like a bad idea")
      case Zone.Battlefield =>
        gameState.updateGameObjectState(_.addNewObject(PermanentObject(card, _, owner), _.length))
      case zone: BasicZone =>
        gameState.updateGameObjectState(_.addNewObject(BasicGameObject(card, _, zone), _.length))
    }
  }
  val playerOne = PlayerId("P1")
  val playerTwo = PlayerId("P2")
  val players = Seq(playerOne, playerTwo)

  val gameStateManager: GameStateManager = {
    val gameStartingData = GameStartingData(Seq(
      PlayerStartingData(playerOne, (Seq.fill(30)(LightningBolt) ++ Seq.fill(30)(Mountain)).map(findCard), Nil),
      PlayerStartingData(playerTwo, (Seq.fill(30)(BeamingDefiance) ++ Seq.fill(30)(Plains)).map(findCard), Nil)))

    val initialManager = GameStateManager.initial(gameStartingData, _ => {})
    val initialGameState = initialManager.gameState

    val cardsToAdd = Seq(
      (Plains, Zone.Battlefield, playerOne),
      (Plains, Zone.Battlefield, playerOne),
      (Plains, Zone.Battlefield, playerOne),
      (Plains, Zone.Battlefield, playerOne),
      (DefendTheCampus, Zone.Hand(playerOne), playerOne),
      (ConcordiaPegasus, Zone.Battlefield, playerOne),
      (ConcordiaPegasus, Zone.Battlefield, playerTwo),
      (GrizzledOutrider, Zone.Battlefield, playerTwo),
      (Plains, Zone.Battlefield, playerTwo),
      (Plains, Zone.Battlefield, playerTwo),
    )

    val updatedState = cardsToAdd.foldLeft(initialGameState) { case (state, (cardDefinition, zone, player)) => addCard(state, cardDefinition, zone, player)}
      .copy(pendingActions = Seq(StartNextTurnAction(playerOne)))
    new GameStateManager(updatedState, onStateUpdate, initialManager.stops)
  }

  def onStateUpdate(gameState: GameState): Unit = {
    players.foreach(player => {
      simpMessagingTemplate.convertAndSendToUser(player.id, "/topic/state", VisibleState.forPlayer(player, gameState))
    })
  }
}
