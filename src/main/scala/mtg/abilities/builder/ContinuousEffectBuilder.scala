package mtg.abilities.builder

import mtg.abilities.AbilityDefinition
import mtg.effects.oneshot.descriptions.{CharacteristicOrControlChangingContinuousEffectDescription, GainsAbilityDescription, GetsPowerToughnessModifierDescription}

trait ContinuousEffectBuilder {
  def gains(abilityDefinition: AbilityDefinition): CharacteristicOrControlChangingContinuousEffectDescription = GainsAbilityDescription(abilityDefinition)
  def get(p: Int, t: Int): CharacteristicOrControlChangingContinuousEffectDescription = GetsPowerToughnessModifierDescription((p, t))
  def gets(p: Int, t: Int): CharacteristicOrControlChangingContinuousEffectDescription = GetsPowerToughnessModifierDescription((p, t))
}
