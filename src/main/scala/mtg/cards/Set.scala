package mtg.cards

import java.time.LocalDate
import scala.collection.mutable.ListBuffer

case class Set(name: String, code: String, releaseDate: LocalDate, cardDataList: Seq[CardInSetData]) {
  val cardPrintings: Seq[CardPrinting] = cardDataList.map(_.toPrinting(this))
  val cardPrintingsByDefinition: Map[CardDefinition, CardPrinting] = cardPrintings.map(p => p.cardDefinition -> p).toMap
  def getCard(cardDefinition: CardDefinition): Option[CardPrinting] = cardPrintings.find(_.cardDefinition == cardDefinition)

  def scryfallUrl: String = s"https://scryfall.com/sets/${code.toLowerCase}"

  override def toString: String = code

  Set._all.addOne(this)
}

object Set {
  private val _all = ListBuffer[Set]()
  def All: List[Set] = _all.result().sortBy(_.releaseDate)(implicitly[Ordering[LocalDate]].reverse)
}
