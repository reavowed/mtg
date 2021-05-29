package mtg.helpers

import mtg.cards.{CardDefinition, CardPrinting}

trait CardHelpers {
  def getCardPrinting(cardDefinition: CardDefinition): CardPrinting = {
    mtg.cards.Set.All.mapFind(_.cardPrintingsByDefinition.get(cardDefinition)).get
  }
}
