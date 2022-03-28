package mtg.instructions.descriptions

import mtg.continuousEffects.CharacteristicOrControlChangingContinuousEffect
import mtg.core.ObjectId
import mtg.text.Verb

trait CharacteristicOrControlChangingContinuousEffectDescription extends Verb {
  def getEffect(objectId: ObjectId): CharacteristicOrControlChangingContinuousEffect
}
