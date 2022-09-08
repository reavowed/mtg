package mtg.cards.text

import mtg.abilities.{AbilityDefinition, AbilityParagraph, KeywordAbility, SpellAbility}
import mtg.definitions.PlayerId
import mtg.instructions.{Instruction, IntransitiveInstructionVerb, TextComponent}

trait TextParagraph extends TextComponent {
  def abilityDefinitions: Seq[AbilityDefinition]
}
trait SingleAbilityTextParagraph extends TextParagraph {
  def abilityDefinition: AbilityParagraph
  override def abilityDefinitions: Seq[AbilityDefinition] = Seq(abilityDefinition)
}

object TextParagraph {
  implicit def fromSingleVerb(intransitiveVerbInstruction: IntransitiveInstructionVerb[PlayerId]): TextParagraph = SpellAbility(InstructionParagraph.fromSingleVerb(intransitiveVerbInstruction))
  implicit def fromSingleInstruction(instruction: Instruction): TextParagraph = SpellAbility(InstructionParagraph.fromSingleInstruction(instruction))
  implicit def seqFromSingleVerb(intransitiveVerbInstruction: IntransitiveInstructionVerb[PlayerId]): Seq[TextParagraph] = Seq(fromSingleVerb(intransitiveVerbInstruction))
  implicit def seqFromSingleInstruction(instruction: Instruction): Seq[TextParagraph] = Seq(fromSingleInstruction(instruction))
  implicit def seqFromSingleSentence(sentence: InstructionSentence): Seq[TextParagraph] = seqFromSingleParagraph(SimpleInstructionParagraph(sentence))
  implicit def seqFromSingleParagraph(paragraph: InstructionParagraph): Seq[TextParagraph] = Seq(SpellAbility(paragraph))
  implicit def fromKeywordAbility(keywordAbility: KeywordAbility): TextParagraph = KeywordAbilityParagraph(Seq(keywordAbility))
  implicit def seqFromKeywordAbility(keywordAbility: KeywordAbility): Seq[TextParagraph] = Seq(fromKeywordAbility(keywordAbility))
  implicit def seqFromSingle[T <: TextParagraph](textParagraph: T): Seq[T] = Seq(textParagraph)
}
