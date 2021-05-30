package mtg.abilities

import mtg.effects.Effect

case class EffectText(paragraphs: Seq[EffectParagraph])

case class EffectParagraph(sentences: EffectSentence*) {
  def getText(cardName: String): String = sentences.map(_.getText(cardName)).mkString(" ")
  def effects: Seq[Effect] = sentences.flatMap(_.effects)
}
object EffectParagraph {
  implicit def sentenceToParagraph(sentence: EffectSentence): EffectParagraph = EffectParagraph(sentence)
  implicit def effectToParagraph(effect: Effect): EffectParagraph = sentenceToParagraph(EffectSentence.effectToSentence(effect))
}

sealed trait EffectSentence {
  def effects: Seq[Effect]
  def getText(cardName: String): String
}
object EffectSentence {
  case class SingleClause(effect: Effect) extends EffectSentence {
    def effects: Seq[Effect] = Seq(effect)
    override def getText(cardName: String): String = effect.getText(cardName).capitalize + "."
  }
  case class MultiClause(effects: Seq[Effect], joiner: String) extends EffectSentence {
    override def getText(cardName: String): String = {
      val clausesText = (
        effects.head.getText(cardName).capitalize +:
          (if (effects.length > 2) effects.tail.init.map(_.getText(cardName)) else Nil)
        ) :+ (joiner + " " + effects.last.getText(cardName))
      clausesText.mkString(", ") + "."
    }
  }
  implicit def effectToSentence(effect: Effect): EffectSentence = SingleClause(effect)
}
