package mtg.abilities.builder

import mtg.abilities.AbilityDefinition
import mtg.effects.oneshot.descriptions.{ContinuousEffectDescription, GainsAbilityDescription, GetsPowerToughnessModifierDescription}

trait ContinuousEffectBuilder {
  def gains(abilityDefinition: AbilityDefinition): ContinuousEffectDescription = GainsAbilityDescription(abilityDefinition)
  def get(p: Int, t: Int): ContinuousEffectDescription = GetsPowerToughnessModifierDescription((p, t))
  def gets(p: Int, t: Int): ContinuousEffectDescription = GetsPowerToughnessModifierDescription((p, t))
}
