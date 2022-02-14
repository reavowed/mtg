package mtg.helpers

import mtg.cards.CardDefinition
import mtg.game.Zone.BasicZone
import mtg.game.objects._
import mtg.game.{PlayerId, TypedZone, Zone}

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
    def getPermanent(cardDefinition: CardDefinition, owner: PlayerId): PermanentObject = {
      gameObjectState.battlefield.getMatching(o => o.isCard(cardDefinition) && o.owner == owner)
    }
    def getCard(cardDefinition: CardDefinition): GameObject = {
      gameObjectState.allObjects.getCard(cardDefinition)
    }
    def getCards(cardDefinitions: CardDefinition*): Seq[GameObject] = {
      cardDefinitions.map(getCard)
    }
    def getCard(zone: Zone, cardDefinition: CardDefinition): GameObject = {
      gameObjectState.getZoneState(zone).getCard(cardDefinition)
    }

    def clearZone[T <: GameObject](zone: TypedZone[T]): GameObjectState = {
      gameObjectState.updateZoneState(zone)(_ => Nil)
    }
    def addCardToZone(cardDefinition: CardDefinition, zone: Zone, owner: PlayerId): GameObjectState = {
      val cardPrinting = getCardPrinting(cardDefinition)
      val card = Card(owner, cardPrinting)
      zone match {
        case Zone.Stack =>
          throw new Exception("Trying to create things directly on the stack seems like a bad idea")
        case Zone.Battlefield =>
          gameObjectState.createObject(PermanentObject(card, _, owner), _.length)
        case zone: BasicZone =>
          gameObjectState.createObject(BasicGameObject(card, _, zone), _.length)
      }
    }
    def addCardsToZone(zone: Zone, owner: PlayerId, cardDefinitions: Seq[CardDefinition]): GameObjectState = {
      cardDefinitions.foldLeft(gameObjectState) { _.addCardToZone(_, zone, owner)}
    }
    def clearZoneAndAddCards[T <: GameObject](zone: TypedZone[T], owner: PlayerId, cardDefinitions: Seq[CardDefinition]): GameObjectState = {
      clearZone(zone).addCardsToZone(zone, owner, cardDefinitions)
    }

    class ZoneSetter(setZone: (PlayerId, Seq[CardDefinition]) => GameObjectState) {
      def apply(playerIdentifier: PlayerId, cardDefinitions: Seq[CardDefinition]): GameObjectState = {
        setZone(playerIdentifier, cardDefinitions)
      }
      def apply(playerIdentifier: PlayerId, cardDefinition: CardDefinition): GameObjectState = {
        setZone(playerIdentifier, Seq(cardDefinition))
      }
      def apply(playerIdentifier: PlayerId, cardDefinition: CardDefinition, number: Int): GameObjectState = {
        setZone(playerIdentifier, Seq.fill(number)(cardDefinition))
      }
    }
    def setHand: ZoneSetter = new ZoneSetter((playerIdentifier, cardDefinitions) => clearZoneAndAddCards(Zone.Hand(playerIdentifier), playerIdentifier, cardDefinitions))
    def setLibrary: ZoneSetter = new ZoneSetter((playerIdentifier, cardDefinitions) => clearZoneAndAddCards(Zone.Library(playerIdentifier), playerIdentifier, cardDefinitions))
    def setBattlefield: ZoneSetter = new ZoneSetter((playerIdentifier, cardDefinitions) =>
      gameObjectState.updateZoneState(Zone.Battlefield)(_.filter(_.defaultController != playerIdentifier))
        .addCardsToZone(Zone.Battlefield, playerIdentifier, cardDefinitions)
    )
  }

}
