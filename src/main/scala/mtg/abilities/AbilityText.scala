package mtg.abilities

import mtg.effects.Effect

case class AbilityText(paragraphs: Seq[AbilityParagraph])

case class AbilityParagraph(sentences: AbilitySentence*) {
  def getText(cardName: String): String = sentences.map(_.getText(cardName)).mkString(" ")
  def effects: Seq[Effect] = sentences.flatMap(_.effects)
}
object AbilityParagraph {
  implicit def sentenceToParagraph(sentence: AbilitySentence): AbilityParagraph = AbilityParagraph(sentence)
  implicit def effectToParagraph(effect: Effect): AbilityParagraph = sentenceToParagraph(AbilitySentence.effectToSentence(effect))
}

sealed trait AbilitySentence {
  def effects: Seq[Effect]
  def getText(cardName: String): String
}
object AbilitySentence {
  case class SingleClause(effect: Effect) extends AbilitySentence {
    def effects: Seq[Effect] = Seq(effect)
    override def getText(cardName: String): String = effect.getText(cardName).capitalize + "."
  }
  case class MultiClause(effects: Seq[Effect], joiner: String) extends AbilitySentence {
    override def getText(cardName: String): String = {
      val clausesText = (
        effects.head.getText(cardName).capitalize +:
          (if (effects.length > 2) effects.tail.init.map(_.getText(cardName)) else Nil)
        ) :+ (joiner + " " + effects.last.getText(cardName))
      clausesText.mkString(", ") + "."
    }
  }
  implicit def effectToSentence(effect: Effect): AbilitySentence = SingleClause(effect)
}
