package mtg.cards

case class Set(name: String, code: String, cardDataList: Seq[CardInSetData]) {
  val cardPrintings: Seq[CardPrinting] = cardDataList.map(_.toPrinting(this))

  def scryfallUrl: String = s"https://scryfall.com/sets/${code.toLowerCase}"
}
