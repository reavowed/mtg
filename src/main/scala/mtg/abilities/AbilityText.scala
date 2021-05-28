package mtg.abilities

import mtg.effects.Effect

case class AbilityText(paragraphs: Seq[AbilityParagraph])

case class AbilityParagraph(sentences: Seq[AbilitySentence]) {
  def text: String = sentences.map(_.text).mkString(" ")
  def effects: Seq[Effect] = sentences.flatMap(_.effects)
}
object AbilityParagraph {
  implicit def sentenceToParagraph(sentence: AbilitySentence): AbilityParagraph = AbilityParagraph(Seq(sentence))
  implicit def effectToParagraph(effect: Effect): AbilityParagraph = sentenceToParagraph(AbilitySentence.effectToSentence(effect))
}

sealed trait AbilitySentence {
  def effects: Seq[Effect]
  def text: String
}
object AbilitySentence {
  case class SingleClause(effect: Effect) extends AbilitySentence {
    def effects: Seq[Effect] = Seq(effect)
    override def text: String = effect.text.capitalize + "."
  }
  case class MultiClause(effects: Seq[Effect], joiner: String) extends AbilitySentence {
    override def text: String = {
      val clausesText = (effects.head.text.capitalize +: (if (effects.length > 2) effects.tail.init.map(_.text) else Nil)) :+ (joiner + " " + effects.last.text)
      clausesText.mkString(", ")
    }
  }
  implicit def effectToSentence(effect: Effect): AbilitySentence = SingleClause(effect)
}
