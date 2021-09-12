package mtg.effects.continuous

import mtg.abilities.AbilityDefinition
import mtg.effects.ContinuousEffect
import mtg.game.ObjectId

case class AddAbilityEffect(affectedObject: ObjectId, abilityDefinition: AbilityDefinition) extends ContinuousEffect.ForSingleObject
