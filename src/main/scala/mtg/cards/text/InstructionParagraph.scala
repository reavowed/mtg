package mtg.cards.text

import mtg.definitions.PlayerId
import mtg.definitions.zones.ZoneType
import mtg.instructions.{Instruction, IntransitiveInstructionVerb, TextComponent}

sealed trait InstructionParagraph extends TextComponent {
  override def getText(cardName: String): String = getUncapitalizedText(cardName).capitalize
  def getUncapitalizedText(cardName: String): String
  def instructions: Seq[Instruction]
  def functionalZones: Option[Set[ZoneType]] = instructions.foldLeft[Option[Set[ZoneType]]](None) {
    case (None, i) =>
      i.functionalZones
    case (Some(zones), i) =>
      i.functionalZones match {
        case Some(moreZones) =>
          Some(zones ++ moreZones)
        case None =>
          Some(zones)
      }
  }
}
object InstructionParagraph {
  implicit def fromSingleVerb(intransitiveVerbInstruction: IntransitiveInstructionVerb[PlayerId]): SimpleInstructionParagraph = fromSingleInstruction(IntransitiveInstructionVerb.Imperative(intransitiveVerbInstruction))
  implicit def fromSingleInstruction(instruction: Instruction): SimpleInstructionParagraph = SimpleInstructionParagraph(InstructionSentence.SingleClause(instruction))
  implicit def seqFromSingleVerb(intransitiveVerbInstruction: IntransitiveInstructionVerb[PlayerId]): Seq[SimpleInstructionParagraph] = Seq(fromSingleVerb(intransitiveVerbInstruction))
  implicit def seqFromSingleInstruction(instruction: Instruction): Seq[SimpleInstructionParagraph] = Seq(fromSingleInstruction(instruction))
  implicit def seqFromSingleSentence(sentence: InstructionSentence): Seq[SimpleInstructionParagraph] = Seq(SimpleInstructionParagraph(sentence))
  implicit def seqFromSingleParagraph(paragraph: InstructionParagraph): Seq[InstructionParagraph] = Seq(paragraph)
}

case class SimpleInstructionParagraph(sentences: InstructionSentence*) extends InstructionParagraph {
  def getUncapitalizedText(cardName: String): String = (sentences.head.getUncapitalizedText(cardName) +: sentences.tail.map(_.getText(cardName))).mkString(" ")
  def instructions: Seq[Instruction] = sentences.flatMap(_.instructions)
}
case class ModalInstructionParagraph(modes: SimpleInstructionParagraph*) extends InstructionParagraph {
  override def getUncapitalizedText(cardName: String): String = ("choose one —" +: modes.map(_.getText(cardName)).map("• " + _)).mkString("\n")
  def instructions: Seq[Instruction] = modes.flatMap(_.instructions)
}

trait InstructionSentence extends TextComponent {
  override def getText(cardName: String): String = getUncapitalizedText(cardName).capitalize
  def getUncapitalizedText(cardName: String): String
  def instructions: Seq[Instruction]
}
object InstructionSentence {
  case class SingleClause(instruction: Instruction) extends InstructionSentence {
    def instructions: Seq[Instruction] = Seq(instruction)
    override def getUncapitalizedText(cardName: String): String = instruction.getText(cardName) + "."
  }
  case class MultiClause(instructions: Instruction*) extends InstructionSentence {
    override def getUncapitalizedText(cardName: String): String = {
      val clausesText = (
        instructions.head.getText(cardName) +:
          (if (instructions.length > 2) instructions.tail.init.map(_.getText(cardName)) else Nil)
        ) :+ ("then " + instructions.last.getText(cardName))
      clausesText.mkString(", ") + "."
    }
  }
  implicit def fromSingleVerb(intransitiveVerbInstruction: IntransitiveInstructionVerb[PlayerId]): InstructionSentence = fromSingleInstruction(IntransitiveInstructionVerb.Imperative(intransitiveVerbInstruction))
  implicit def fromSingleInstruction(instruction: Instruction): InstructionSentence = SingleClause(instruction)
}
