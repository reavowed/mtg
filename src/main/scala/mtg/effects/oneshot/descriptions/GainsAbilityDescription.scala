package mtg.effects.oneshot.descriptions

import mtg.abilities.AbilityDefinition
import mtg.effects.ContinuousEffect
import mtg.effects.continuous.AddAbilityEffect
import mtg.game.ObjectId
import mtg.text.{VerbPhraseTemplate, Verbs}

case class GainsAbilityDescription(ability: AbilityDefinition) extends ContinuousEffectDescription {
  override def getVerbPhraseTemplate(cardName: String): VerbPhraseTemplate = Verbs.Gain.withSuffix(ability.getQuotedDescription(cardName))
  override def getEffect(objectId: ObjectId): ContinuousEffect = AddAbilityEffect(objectId, ability)
}
