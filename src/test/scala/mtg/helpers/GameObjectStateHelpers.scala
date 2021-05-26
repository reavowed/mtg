package mtg.helpers

import mtg.cards.CardDefinition
import mtg.game.objects.{Card, CardObject, GameObjectState, ObjectId}
import mtg.game.{PlayerIdentifier, Zone}

trait GameObjectStateHelpers extends CardHelpers {
  implicit class GameObjectStateExtensions(gameObjectState: GameObjectState) {
    def clearZone(zone: Zone): GameObjectState = {
      gameObjectState.updateZone(zone, _ => Nil)
    }
    def addCardToZone(cardDefinition: CardDefinition, zone: Zone, owner: PlayerIdentifier): GameObjectState = {
      val cardPrinting = getCardPrinting(cardDefinition)
      val card = Card(owner, cardPrinting)
      val defaultController = if (zone == Zone.Battlefield || zone == Zone.Stack) Some(owner) else None
      val cardObject = CardObject(card, ObjectId(gameObjectState.nextObjectId), zone, defaultController, zone.defaultPermanentStatus, 0)
      gameObjectState.updateZone(zone, _ :+ cardObject).copy(nextObjectId = gameObjectState.nextObjectId + 1)
    }
    def addCardsToZone(zone: Zone, owner: PlayerIdentifier, cardDefinitions: Seq[CardDefinition]): GameObjectState = {
      cardDefinitions.foldLeft(gameObjectState) { _.addCardToZone(_, zone, owner)}
    }
    def clearZoneAndAddCards(zone: Zone, owner: PlayerIdentifier, cardDefinitions: Seq[CardDefinition]): GameObjectState = {
      clearZone(zone).addCardsToZone(zone, owner, cardDefinitions)
    }
    def setLibrary(playerIdentifier: PlayerIdentifier, cardDefinitions: Seq[CardDefinition]): GameObjectState = {
      clearZoneAndAddCards(Zone.Library(playerIdentifier), playerIdentifier, cardDefinitions)
    }

    class ZoneSetter(setZone: (PlayerIdentifier, Seq[CardDefinition]) => GameObjectState) {
      def apply(playerIdentifier: PlayerIdentifier, cardDefinitions: Seq[CardDefinition]): GameObjectState = {
        setZone(playerIdentifier, cardDefinitions)
      }
      def apply(playerIdentifier: PlayerIdentifier, cardDefinition: CardDefinition): GameObjectState = {
        setZone(playerIdentifier, Seq(cardDefinition))
      }
      def apply(playerIdentifier: PlayerIdentifier, cardDefinition: CardDefinition, number: Int): GameObjectState = {
        setZone(playerIdentifier, Seq.fill(number)(cardDefinition))
      }
    }
    def setHand: ZoneSetter = new ZoneSetter((playerIdentifier, cardDefinitions) => clearZoneAndAddCards(Zone.Hand(playerIdentifier), playerIdentifier, cardDefinitions))
    def setBattlefield: ZoneSetter = new ZoneSetter((playerIdentifier, cardDefinitions) =>
      gameObjectState.updateZone(Zone.Battlefield, _.filter(!_.defaultController.contains(playerIdentifier)))
        .addCardsToZone(Zone.Battlefield, playerIdentifier, cardDefinitions)
    )
  }

}
