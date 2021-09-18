package mtg.data

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ArrayNode
import mtg.data.cards.{Forest, Island, Mountain, Plains, Swamp}
import org.specs2.mutable.Specification

import java.io.{File, FileInputStream}
import scala.annotation.tailrec
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

  "card data" should {
    "match oracle data" in {
      val objectMapper = new ObjectMapper()
      val oracleDataFile = new FileInputStream(new File(Thread.currentThread().getContextClassLoader.getResource("oracle-cards-20210918090350.json").getPath))
      val oracleData = objectMapper.readTree(oracleDataFile).asInstanceOf[ArrayNode]
      val cardPrintings = mtg.cards.Set.All.flatMap(_.cardPrintings).filter(p => !Seq(Plains, Island, Swamp, Mountain, Forest).contains(p.cardDefinition))
      foreach(cardPrintings)(cardPrinting => {
        val data = oracleData.iterator().asScala.find(element =>
          element.get("name").textValue() == cardPrinting.cardDefinition.name
        )
        data must beSome
        (stripReminderText(data.get.get("oracle_text").textValue()) aka cardPrinting.cardDefinition.name) mustEqual cardPrinting.cardDefinition.text
      })
    }
  }
}
