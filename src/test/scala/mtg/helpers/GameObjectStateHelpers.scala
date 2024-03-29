package mtg.helpers

import monocle.Focus
import mtg.cards.CardDefinition
import mtg.definitions.PlayerId
import mtg.definitions.zones.Zone
import mtg.definitions.zones.Zone.BasicZone
import mtg.game.objects._
import mtg.parts.Counter

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
          gameObjectState.addObjectToBattlefield(PermanentObject(card, _, owner))._2
        case Zone.Hand(player) =>
          gameObjectState.addObjectToHand(player, BasicGameObject(card, _, Zone.Hand(player)))._2
        case Zone.Library(player) =>
          gameObjectState.addObjectToLibrary(player, BasicGameObject(card, _, Zone.Library(player)), _.length)._2
        case Zone.Graveyard(player) =>
          gameObjectState.addObjectToGraveyard(player, BasicGameObject(card, _, Zone.Graveyard(player)))._2
        case Zone.Sideboard(player) =>
          gameObjectState.addObjectToSideboard(player, BasicGameObject(card, _, Zone.Sideboard(player)))._2
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
    def setGraveyard: ZoneSetter = new ZoneSetter((playerIdentifier, cardDefinitions) => clearZoneAndAddCards(Zone.Graveyard(playerIdentifier), playerIdentifier, cardDefinitions))
    def setLibrary: ZoneSetter = new ZoneSetter((playerIdentifier, cardDefinitions) => clearZoneAndAddCards(Zone.Library(playerIdentifier), playerIdentifier, cardDefinitions))
    def setBattlefield: ZoneSetter = new ZoneSetter((playerIdentifier, cardDefinitions) =>
      Focus[GameObjectState](_.battlefield).modify(_.filter(_.defaultController != playerIdentifier))(gameObjectState)
        .addCardsToZone(Zone.Battlefield, playerIdentifier, cardDefinitions)
    )
    def addPermanentObject(player: PlayerId, cardDefinition: CardDefinition, counters: Map[Counter, Int] = Map.empty): GameObjectState = {
      gameObjectState.addObjectToBattlefield(PermanentObject(Card(player, getCardPrinting(cardDefinition)), _, player, counters))._2
    }
    def setSideboard: ZoneSetter = new ZoneSetter((playerIdentifier, cardDefinitions) => clearZoneAndAddCards(Zone.Sideboard(playerIdentifier), playerIdentifier, cardDefinitions))
  }

}
