package mtg.cards.text

import mtg.abilities.{AbilityDefinition, SpellAbility}
import mtg.effects.OneShotEffect

sealed trait InstructionParagraph extends TextParagraph {
  override def abilityDefinitions: Seq[AbilityDefinition] = Seq(SpellAbility(this))
}
object InstructionParagraph {
  implicit def fromSingleEffect(effect: OneShotEffect): SimpleInstructionParagraph = SimpleInstructionParagraph(InstructionSentence.SingleClause(effect))
  implicit def seqFromSingleEffect(effect: OneShotEffect): Seq[SimpleInstructionParagraph] = Seq(fromSingleEffect(effect))
  implicit def seqFromSingleSentence(sentence: InstructionSentence): Seq[SimpleInstructionParagraph] = Seq(SimpleInstructionParagraph(sentence))
  implicit def seqFromSingleParagraph(paragraph: InstructionParagraph): Seq[InstructionParagraph] = Seq(paragraph)
}

case class SimpleInstructionParagraph(sentences: InstructionSentence*) extends InstructionParagraph {
  def getText(cardName: String): String = sentences.map(_.getText(cardName)).mkString(" ")
  def effects: Seq[OneShotEffect] = sentences.flatMap(_.effects)
}
case class ModalInstructionParagraph(modes: SimpleInstructionParagraph*) extends InstructionParagraph {
  override def getText(cardName: String): String = ("Choose one —" +: modes.map(_.getText(cardName)).map("• " + _)).mkString("\n")
}

trait InstructionSentence {
  def effects: Seq[OneShotEffect]
  def getText(cardName: String): String
}
object InstructionSentence {
  case class SingleClause(effect: OneShotEffect) extends InstructionSentence {
    def effects: Seq[OneShotEffect] = Seq(effect)
    override def getText(cardName: String): String = effect.getText(cardName).capitalize + "."
  }
  case class MultiClause(effects: Seq[OneShotEffect], joiner: String) extends InstructionSentence {
    override def getText(cardName: String): String = {
      val clausesText = (
        effects.head.getText(cardName).capitalize +:
          (if (effects.length > 2) effects.tail.init.map(_.getText(cardName)) else Nil)
        ) :+ (joiner + " " + effects.last.getText(cardName))
      clausesText.mkString(", ") + "."
    }
  }
  implicit def effectToSentence(effect: OneShotEffect): InstructionSentence = SingleClause(effect)
}
