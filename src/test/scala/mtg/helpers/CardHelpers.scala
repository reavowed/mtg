package mtg.helpers

import mtg.cards.{CardDefinition, CardPrinting}
import mtg.game.PlayerIdentifier
import mtg.game.objects.{Card, CardObject}

trait CardHelpers {
  def getCardPrinting(cardDefinition: CardDefinition): CardPrinting = {
    mtg.cards.Set.All.mapFind(_.cardPrintingsByDefinition.get(cardDefinition)).get
  }
}
