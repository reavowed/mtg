package mtg.effects.oneshot.descriptions

import mtg.core.ObjectId
import mtg.effects.continuous.CharacteristicOrControlChangingContinuousEffect
import mtg.text.VerbPhraseTemplate

trait CharacteristicOrControlChangingContinuousEffectDescription {
  def getVerbPhraseTemplate(cardName: String): VerbPhraseTemplate
  def getEffect(objectId: ObjectId): CharacteristicOrControlChangingContinuousEffect
}
