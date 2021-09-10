package mtg.effects.continuous

import mtg.abilities.AbilityDefinition
import mtg.effects.ContinuousObjectEffect
import mtg.game.ObjectId

case class AddAbilityEffect(affectedObject: ObjectId, abilityDefinition: AbilityDefinition) extends ContinuousObjectEffect
