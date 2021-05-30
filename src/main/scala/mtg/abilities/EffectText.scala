package mtg.abilities

import mtg.effects.oneshot.OneShotEffect

case class EffectText(paragraphs: Seq[EffectParagraph])

case class EffectParagraph(sentences: EffectSentence*) {
  def getText(cardName: String): String = sentences.map(_.getText(cardName)).mkString(" ")
  def effects: Seq[OneShotEffect] = sentences.flatMap(_.effects)
}
object EffectParagraph {
  implicit def sentenceToParagraph(sentence: EffectSentence): EffectParagraph = EffectParagraph(sentence)
  implicit def effectToParagraph(effect: OneShotEffect): EffectParagraph = sentenceToParagraph(EffectSentence.effectToSentence(effect))
}

sealed trait EffectSentence {
  def effects: Seq[OneShotEffect]
  def getText(cardName: String): String
}
object EffectSentence {
  case class SingleClause(effect: OneShotEffect) extends EffectSentence {
    def effects: Seq[OneShotEffect] = Seq(effect)
    override def getText(cardName: String): String = effect.getText(cardName).capitalize + "."
  }
  case class MultiClause(effects: Seq[OneShotEffect], joiner: String) extends EffectSentence {
    override def getText(cardName: String): String = {
      val clausesText = (
        effects.head.getText(cardName).capitalize +:
          (if (effects.length > 2) effects.tail.init.map(_.getText(cardName)) else Nil)
        ) :+ (joiner + " " + effects.last.getText(cardName))
      clausesText.mkString(", ") + "."
    }
  }
  implicit def effectToSentence(effect: OneShotEffect): EffectSentence = SingleClause(effect)
}
