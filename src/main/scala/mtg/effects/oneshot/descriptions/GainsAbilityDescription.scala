package mtg.effects.oneshot.descriptions

import mtg.abilities.AbilityDefinition
import mtg.effects.{ContinuousEffect, EffectContext}
import mtg.effects.continuous.AddAbilityEffect
import mtg.game.ObjectId

case class GainsAbilityDescription(ability: AbilityDefinition) extends ContinuousEffectDescription {
  override def getText(cardName: String): String = "gains " + ability.getQuotedDescription(cardName)
  override def getEffect(objectId: ObjectId): ContinuousEffect = AddAbilityEffect(objectId, ability)
}
