package mtg.helpers

import monocle.Focus
import mtg.cards.CardDefinition
import mtg.core.PlayerId
import mtg.core.zones.Zone
import mtg.core.zones.Zone.BasicZone
import mtg.game.objects._

trait GameObjectStateHelpers extends CardHelpers with GameObjectHelpers {
  implicit class GameObjectStateExtensions(gameObjectState: GameObjectState) {

    def getZoneState(zone: Zone): Seq[GameObject] = {
      zone match {
        case Zone.Library(player) => gameObjectState.libraries(player)
        case Zone.Hand(player) => gameObjectState.hands(player)
        case Zone.Graveyard(player) => gameObjectState.graveyards(player)
        case Zone.Battlefield => gameObjectState.battlefield
        case Zone.Stack => gameObjectState.stack
        case Zone.Exile => gameObjectState.exile
      }
    }

    def getPermanent(cardDefinition: CardDefinition): PermanentObject = {
      gameObjectState.battlefield.getCard(cardDefinition)
    }
    def getPermanent(cardDefinition: CardDefinition, controller: PlayerId): PermanentObject = {
      gameObjectState.battlefield.getMatching(o => o.isCard(cardDefinition) && gameObjectState.derivedState.permanentStates(o.objectId).controller == controller)
    }
    def getCard(cardDefinition: CardDefinition): GameObject = {
      gameObjectState.allObjects.getCard(cardDefinition)
    }
    def getCard(cardDefinition: CardDefinition, owner: PlayerId): GameObject = {
      gameObjectState.allObjects.getMatching(o => o.isCard(cardDefinition) && o.owner == owner)
    }
    def getCards(cardDefinitions: CardDefinition*): Seq[GameObject] = {
      cardDefinitions.map(getCard)
    }
    def getCard(zone: Zone, cardDefinition: CardDefinition): GameObject = {
      gameObjectState.getZoneState(zone).getCard(cardDefinition)
    }

    def clearZone(zone: BasicZone): GameObjectState = {
      gameObjectState.updateZone(zone, _ => Nil)
    }
    def addCardToZone(cardDefinition: CardDefinition, zone: Zone, owner: PlayerId): GameObjectState = {
      val cardPrinting = getCardPrinting(cardDefinition)
      val card = Card(owner, cardPrinting)
      zone match {
        case Zone.Stack =>
          throw new Exception("Trying to create things directly on the stack seems like a bad idea")
        case Zone.Battlefield =>
          gameObjectState.addObjectToBattlefield(PermanentObject(card, _, owner))
        case Zone.Hand(player) =>
          gameObjectState.addObjectToHand(player, BasicGameObject(card, _, Zone.Hand(player)))
        case Zone.Library(player) =>
          gameObjectState.addObjectToLibrary(player, BasicGameObject(card, _, Zone.Library(player)), _.length)
      }
    }
    def addCardsToZone(zone: Zone, owner: PlayerId, cardDefinitions: Seq[CardDefinition]): GameObjectState = {
      cardDefinitions.foldLeft(gameObjectState) { _.addCardToZone(_, zone, owner)}
    }
    def clearZoneAndAddCards(zone: BasicZone, owner: PlayerId, cardDefinitions: Seq[CardDefinition]): GameObjectState = {
      clearZone(zone).addCardsToZone(zone, owner, cardDefinitions)
    }

    class ZoneSetter(setZone: (PlayerId, Seq[CardDefinition]) => GameObjectState) {
      def apply(playerIdentifier: PlayerId, cardDefinitions: CardDefinition*): GameObjectState = {
        setZone(playerIdentifier, cardDefinitions)
      }
      def apply(playerIdentifier: PlayerId, cardDefinition: CardDefinition, number: Int): GameObjectState = {
        setZone(playerIdentifier, Seq.fill(number)(cardDefinition))
      }
    }
    def setHand: ZoneSetter = new ZoneSetter((playerIdentifier, cardDefinitions) => clearZoneAndAddCards(Zone.Hand(playerIdentifier), playerIdentifier, cardDefinitions))
    def setLibrary: ZoneSetter = new ZoneSetter((playerIdentifier, cardDefinitions) => clearZoneAndAddCards(Zone.Library(playerIdentifier), playerIdentifier, cardDefinitions))
    def setBattlefield: ZoneSetter = new ZoneSetter((playerIdentifier, cardDefinitions) =>
      Focus[GameObjectState](_.battlefield).modify(_.filter(_.defaultController != playerIdentifier))(gameObjectState)
        .addCardsToZone(Zone.Battlefield, playerIdentifier, cardDefinitions)
    )
  }

}
