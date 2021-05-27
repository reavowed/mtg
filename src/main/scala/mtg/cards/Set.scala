package mtg.cards

import org.reflections.Reflections

import java.time.LocalDate
import scala.jdk.CollectionConverters._

case class Set(name: String, code: String, releaseDate: LocalDate, cardDataList: Seq[CardInSetData]) {
  val cardPrintings: Seq[CardPrinting] = cardDataList.map(_.toPrinting(this))
  val cardPrintingsByDefinition: Map[CardDefinition, CardPrinting] = cardPrintings.map(p => p.cardDefinition -> p).toMap
  def getCard(cardDefinition: CardDefinition): Option[CardPrinting] = cardPrintings.find(_.cardDefinition == cardDefinition)

  def scryfallUrl: String = s"https://scryfall.com/sets/${code.toLowerCase}"

  override def toString: String = code
}

object Set {
  val All: Seq[Set] = {
    val subtypes = new Reflections("mtg.data.sets").getSubTypesOf(classOf[mtg.cards.Set]).asScala.toSeq
    val rootMirror = scala.reflect.runtime.universe.runtimeMirror(getClass.getClassLoader)
    val sets = subtypes.map(t => rootMirror.reflectModule(rootMirror.classSymbol(t).module.asModule).instance).map(_.asInstanceOf[Set])
    sets.sortBy(_.releaseDate)(implicitly[Ordering[LocalDate]].reverse)
  }
}
