package mtg.cards.text

import mtg.abilities.{AbilityDefinition, ActivatedAbilityDefinition}
import mtg.effects.EffectContext

class ActivatedAbilityParagraph(activatedAbilityDefinition: ActivatedAbilityDefinition) extends TextParagraph {
  override def getText(cardName: String): String = activatedAbilityDefinition.getText(cardName)
  override def abilityDefinitions: Seq[AbilityDefinition] = Seq(activatedAbilityDefinition)
}
