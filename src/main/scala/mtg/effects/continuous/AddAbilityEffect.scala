package mtg.effects.continuous

import mtg.abilities.AbilityDefinition
import mtg.core.ObjectId

case class AddAbilityEffect(affectedObject: ObjectId, abilityDefinition: AbilityDefinition)
  extends CharacteristicOrControlChangingContinuousEffect
