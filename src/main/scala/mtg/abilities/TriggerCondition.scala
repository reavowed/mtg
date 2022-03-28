package mtg.abilities

import mtg.cards.text.InstructionParagraph
import mtg.effects.condition.Condition
import mtg.instructions.{TextComponent, TriggerWord}

case class TriggerCondition(triggerWord: TriggerWord, condition: Condition) extends TextComponent {
  override def getText(cardName: String): String = triggerWord.text + " " + condition.getText(cardName)
  def apply(instructionParagraph: InstructionParagraph): TriggeredAbilityDefinition = {
    TriggeredAbilityDefinition(this, instructionParagraph)
  }
}
