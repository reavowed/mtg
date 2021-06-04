package mtg.cards.text

import mtg.abilities.{AbilityDefinition, SpellAbility}
import mtg.effects.OneShotEffect
import mtg.effects.identifiers.Identifier

case class SpellEffectParagraph(sentences: SpellEffectSentence*) extends TextParagraph {
  def getText(cardName: String): String = sentences.map(_.getText(cardName)).mkString(" ")
  def effects: Seq[OneShotEffect] = sentences.flatMap(_.effects)
  override def abilityDefinitions: Seq[AbilityDefinition] = Seq(SpellAbility(this))
}
object SpellEffectParagraph {
  implicit def fromSingleEffect(effect: OneShotEffect): SpellEffectParagraph = SpellEffectParagraph(SpellEffectSentence.SingleClause(effect))
  implicit def seqFromSingleEffect(effect: OneShotEffect): Seq[SpellEffectParagraph] = Seq(fromSingleEffect(effect))
  implicit def seqFromSingleSentence(sentence: SpellEffectSentence): Seq[SpellEffectParagraph] = Seq(SpellEffectParagraph(sentence))
}

trait SpellEffectSentence {
  def effects: Seq[OneShotEffect]
  def getText(cardName: String): String
}
object SpellEffectSentence {
  case class SingleClause(effect: OneShotEffect) extends SpellEffectSentence {
    def effects: Seq[OneShotEffect] = Seq(effect)
    override def getText(cardName: String): String = effect.getText(cardName).capitalize + "."
  }
  case class MultiClause(effects: Seq[OneShotEffect], joiner: String) extends SpellEffectSentence {
    override def getText(cardName: String): String = {
      val clausesText = (
        effects.head.getText(cardName).capitalize +:
          (if (effects.length > 2) effects.tail.init.map(_.getText(cardName)) else Nil)
        ) :+ (joiner + " " + effects.last.getText(cardName))
      clausesText.mkString(", ") + "."
    }
  }
  implicit def effectToSentence(effect: OneShotEffect): SpellEffectSentence = SingleClause(effect)
}
