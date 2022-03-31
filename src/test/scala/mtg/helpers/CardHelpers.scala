package mtg.helpers

import mtg._
import mtg.cards.{CardDefinition, CardPrinting, Set}

import java.time.LocalDate
import scala.collection.mutable.ListBuffer

trait CardHelpers {
  val TestSet = new Set("Test Set", "TST", LocalDate.MIN, Nil)
  val TestCards = new ListBuffer[CardDefinition]

  private def getTestCardPrinting(cardDefinition: CardDefinition): CardPrinting = {
    TestCards.synchronized {
      var index = TestCards.indexOf(cardDefinition)
      if (index == -1) {
        index = TestCards.length
        TestCards += cardDefinition
      }
      CardPrinting(cardDefinition, TestSet, index + 1)
    }
  }

  def getCardPrinting(cardDefinition: CardDefinition): CardPrinting = {
    mtg.cards.Set.All.mapFind(_.cardPrintingsByDefinition.get(cardDefinition))
      .getOrElse(getTestCardPrinting(cardDefinition))
  }
}
