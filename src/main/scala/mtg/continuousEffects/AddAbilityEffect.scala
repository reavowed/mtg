package mtg.continuousEffects

import mtg.abilities.AbilityDefinition
import mtg.definitions.ObjectId

case class AddAbilityEffect(affectedObject: ObjectId, abilityDefinition: AbilityDefinition)
  extends CharacteristicOrControlChangingContinuousEffect
