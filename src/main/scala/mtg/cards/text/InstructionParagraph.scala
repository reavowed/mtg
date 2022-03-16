package mtg.cards.text

import mtg.abilities.{AbilityDefinition, SpellAbility}
import mtg.cards.text.InstructionParagraph.fromSingleInstruction
import mtg.instructions.{Instruction, IntransitiveInstructionVerb}

sealed trait InstructionParagraph extends TextParagraph {
  override def abilityDefinitions: Seq[AbilityDefinition] = Seq(SpellAbility(this))
}
object InstructionParagraph {
  implicit def fromSingleVerb(intransitiveVerbInstruction: IntransitiveInstructionVerb): SimpleInstructionParagraph = fromSingleInstruction(intransitiveVerbInstruction.imperative)
  implicit def fromSingleInstruction(instruction: Instruction): SimpleInstructionParagraph = SimpleInstructionParagraph(InstructionSentence.SingleClause(instruction))
  implicit def seqFromSingleVerb(intransitiveVerbInstruction: IntransitiveInstructionVerb): Seq[SimpleInstructionParagraph] = Seq(fromSingleVerb(intransitiveVerbInstruction))
  implicit def seqFromSingleInstruction(instruction: Instruction): Seq[SimpleInstructionParagraph] = Seq(fromSingleInstruction(instruction))
  implicit def seqFromSingleSentence(sentence: InstructionSentence): Seq[SimpleInstructionParagraph] = Seq(SimpleInstructionParagraph(sentence))
  implicit def seqFromSingleParagraph(paragraph: InstructionParagraph): Seq[InstructionParagraph] = Seq(paragraph)
}

case class SimpleInstructionParagraph(sentences: InstructionSentence*) extends InstructionParagraph {
  def getText(cardName: String): String = sentences.map(_.getText(cardName)).mkString(" ")
  def instructions: Seq[Instruction] = sentences.flatMap(_.instructions)
}
case class ModalInstructionParagraph(modes: SimpleInstructionParagraph*) extends InstructionParagraph {
  override def getText(cardName: String): String = ("Choose one —" +: modes.map(_.getText(cardName)).map("• " + _)).mkString("\n")
}

trait InstructionSentence {
  def instructions: Seq[Instruction]
  def getText(cardName: String): String
}
object InstructionSentence {
  case class SingleClause(instruction: Instruction) extends InstructionSentence {
    def instructions: Seq[Instruction] = Seq(instruction)
    override def getText(cardName: String): String = instruction.getText(cardName).capitalize + "."
  }
  case class MultiClause(instructions: Instruction*) extends InstructionSentence {
    override def getText(cardName: String): String = {
      val clausesText = (
        instructions.head.getText(cardName).capitalize +:
          (if (instructions.length > 2) instructions.tail.init.map(_.getText(cardName)) else Nil)
        ) :+ ("then " + instructions.last.getText(cardName))
      clausesText.mkString(", ") + "."
    }
  }
  implicit def fromSingleVerb(intransitiveVerbInstruction: IntransitiveInstructionVerb): InstructionSentence = instructionToSentence(intransitiveVerbInstruction.imperative)
  implicit def instructionToSentence(instruction: Instruction): InstructionSentence = SingleClause(instruction)
}
