package mtg.instructions.descriptions

import mtg.continuousEffects.CharacteristicOrControlChangingContinuousEffect
import mtg.core.ObjectId
import mtg.text.VerbPhraseTemplate

trait CharacteristicOrControlChangingContinuousEffectDescription {
  def getVerbPhraseTemplate(cardName: String): VerbPhraseTemplate
  def getEffect(objectId: ObjectId): CharacteristicOrControlChangingContinuousEffect
}
