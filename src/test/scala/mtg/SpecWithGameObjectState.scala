package mtg

import mtg.cards.CardPrinting
import mtg.data.sets.Strixhaven
import mtg.game.objects._
import mtg.game.state.GameState
import mtg.game.{GameData, GameStartingData, PlayerIdentifier, PlayerStartingData, Zone}
import org.specs2.matcher.Matcher
import org.specs2.mutable.SpecificationLike

trait SpecWithGameObjectState extends SpecificationLike {
  val playerOne = PlayerIdentifier("P1")
  val playerTwo = PlayerIdentifier("P2")
  val players = Seq(playerOne, playerTwo)
  val gameData = GameData.initial(players)

  val playerOneAllCards = Strixhaven.cardPrintings
  val playerTwoAllCards = Strixhaven.cardPrintings.reverse

  val (playerOneInitialHand, playerOneInitialLibrary) = playerOneAllCards.splitAt(7)
  val (playerTwoInitialHand, playerTwoInitialLibrary) = playerTwoAllCards.splitAt(7)

  val emptyGameObjectState = GameObjectState.initial(GameStartingData(players.map(PlayerStartingData(_, Nil, Nil))))

  def setInitialHandAndLibrary(gameObjectState: GameObjectState): GameObjectState = {
    gameObjectState
      .setHand(playerOne, playerOneInitialHand)
      .setLibrary(playerOne, playerOneInitialLibrary)
      .setHand(playerTwo, playerTwoInitialHand)
      .setLibrary(playerTwo, playerTwoInitialLibrary)
  }

  val gameObjectStateWithInitialLibrariesOnly = emptyGameObjectState.setLibrary(playerOne, playerOneAllCards).setLibrary(playerTwo, playerTwoAllCards)
  val gameObjectStateWithInitialLibrariesAndHands = setInitialHandAndLibrary(emptyGameObjectState)

  implicit class PlayerOps(playerIdentifier: PlayerIdentifier) {
    def library = Zone.Library(playerIdentifier)
    def hand = Zone.Hand(playerIdentifier)
  }
  implicit class ZoneOps(zone: Zone) {
    def apply(gameState: GameState): Seq[GameObject] = {
      apply(gameState.gameObjectState)
    }
    def apply(gameObjectState: GameObjectState): Seq[GameObject] = {
      zone.stateLens.get(gameObjectState)
    }
  }

  implicit class GameObjectStateOps(gameObjectState: GameObjectState) {
    def setZone(zone: Zone, playerIdentifier: PlayerIdentifier, cardPrintings: Seq[CardPrinting]): GameObjectState = {
      val cards = cardPrintings.map(Card(playerIdentifier, _))
      val defaultController = if (zone == Zone.Battlefield) Some(playerIdentifier) else None
      cards.foldLeft(gameObjectState.updateZone(zone, _ => Nil)) { (state, card) =>
        zone.stateLens.modify(s => s :+ CardObject(card, ObjectId(state.nextObjectId), zone, defaultController, zone.defaultPermanentStatus))(state)
          .copy(nextObjectId = state.nextObjectId + 1)
      }
    }

    def setLibrary(playerIdentifier: PlayerIdentifier, cardPrintings: Seq[CardPrinting]): GameObjectState = {
      setZone(Zone.Library(playerIdentifier), playerIdentifier, cardPrintings)
    }
    def setHand(playerIdentifier: PlayerIdentifier, cardPrintings: Seq[CardPrinting]): GameObjectState = {
      setZone(Zone.Hand(playerIdentifier), playerIdentifier, cardPrintings)
    }
    def setBattlefield(cardPrintingsByPlayer: Map[PlayerIdentifier, Seq[CardPrinting]]): GameObjectState = {
      cardPrintingsByPlayer.foldLeft(gameObjectState) { case (state, (player, cardPrintings)) =>
        state.setZone(Zone.Battlefield, player, cardPrintings)
      }
    }
  }
  implicit class GameObjectOps(gameObject: GameObject) {
    def card: Card = gameObject.asInstanceOf[CardObject].card
  }

  def matchCardObject(card: Card): Matcher[GameObject] = {
    { (gameObject: GameObject) => gameObject.asInstanceOf[CardObject].card } ^^ beEqualTo(card)
  }
}
