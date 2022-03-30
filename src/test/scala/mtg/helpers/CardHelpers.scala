package mtg.helpers

import mtg.cards.{CardDefinition, CardPrinting}
import mtg.data.sets.strixhaven.Strixhaven

trait CardHelpers {
  def getCardPrinting(cardDefinition: CardDefinition): CardPrinting = {
    mtg.cards.Set.All.mapFind(_.cardPrintingsByDefinition.get(cardDefinition))
      .getOrElse(CardPrinting(cardDefinition, Strixhaven, 999))
  }
}
