package mtg.data

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ArrayNode
import mtg.cards.CardDefinition
import mtg.sets.alpha.cards._
import org.specs2.mutable.Specification
import org.springframework.core.io.support.PathMatchingResourcePatternResolver

import java.io.FileInputStream
import scala.annotation.tailrec
import scala.collection.mutable.ListBuffer
import scala.jdk.CollectionConverters._

class DataValidationSpec extends Specification {
  @tailrec
  final def stripReminderText(str: String): String = {
    val openParenIndex = str.indexOf('(')
    if (openParenIndex >= 0) {
      val closeParenIndex =  str.indexOf(')', openParenIndex)
      stripReminderText(str.substring(0, openParenIndex) + str.substring(closeParenIndex + 1))
    } else {
      str.replaceAll("  *", " ").trim
    }
  }

  def constructTypeLine(cardDefinition: CardDefinition): String = {
    val components = ListBuffer[String]()
    components.addAll(cardDefinition.supertypes.map(_.name))
    components.addAll(cardDefinition.types.map(_.name))
    if (cardDefinition.subtypes.nonEmpty) {
      components.addOne("—")
      components.addAll(cardDefinition.subtypes.map(_.name))
    }
    components.mkString(" ")
  }

  "card data" should {
    "match oracle data" in {
      val objectMapper = new ObjectMapper()
      val oracleDataFile = new FileInputStream(new PathMatchingResourcePatternResolver().getResources("classpath*:oracle-cards-*.json").head.getFile)
      val oracleData = objectMapper.readTree(oracleDataFile).asInstanceOf[ArrayNode]
      val cardPrintings = mtg.cards.Set.All.flatMap(_.cardPrintings).filter(p => !Seq(Plains, Island, Swamp, Mountain, Forest).contains(p.cardDefinition))
      foreach(cardPrintings)(cardPrinting => {
        val dataOption = oracleData.iterator().asScala.find(element =>
          element.get("name").textValue() == cardPrinting.cardDefinition.name
        )
        dataOption must beSome
        val data = dataOption.get
        (cardPrinting.cardDefinition.text aka s"${cardPrinting.cardDefinition.name} oracle text") mustEqual stripReminderText(data.get("oracle_text").textValue())
        (constructTypeLine(cardPrinting.cardDefinition) aka s"${cardPrinting.cardDefinition.name} type line") mustEqual data.get("type_line").textValue()
      })
    }
  }
}
