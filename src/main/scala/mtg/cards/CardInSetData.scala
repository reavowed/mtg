package mtg.cards

case class CardInSetData(
  cardDefinition: CardDefinition,
  collectorNumber: Int
) {
  def toPrinting(set: Set): CardPrinting = CardPrinting(cardDefinition, set, collectorNumber)
}
