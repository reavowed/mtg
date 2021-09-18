package mtg.helpers

import mtg.SpecWithGameStateManager
import mtg.cards.{CardDefinition, CardPrinting}
import mtg.data.sets.Strixhaven

trait SpecWithTestCards extends SpecWithGameStateManager {
  def testCards: Seq[CardDefinition]

  override def getCardPrinting(cardDefinition: CardDefinition): CardPrinting = {
    if (testCards.contains(cardDefinition))
      CardPrinting(cardDefinition, Strixhaven, 999)
    else
      super.getCardPrinting(cardDefinition)
  }
}
