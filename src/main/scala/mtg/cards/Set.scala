package mtg.cards

case class Set(name: String, code: String, cardDataList: Seq[CardInSetData]) {
  val cardPrintings: Seq[CardPrinting] = cardDataList.map(_.toPrinting(this))
  val cardPrintingsByDefinition: Map[CardDefinition, CardPrinting] = cardPrintings.map(p => p.cardDefinition -> p).toMap
  def getCard(cardDefinition: CardDefinition): Option[CardPrinting] = cardPrintings.find(_.cardDefinition == cardDefinition)

  def scryfallUrl: String = s"https://scryfall.com/sets/${code.toLowerCase}"

  override def toString: String = code
}
