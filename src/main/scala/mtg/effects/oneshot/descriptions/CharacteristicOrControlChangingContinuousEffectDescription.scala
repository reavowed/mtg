package mtg.effects.oneshot.descriptions

import mtg.effects.continuous.CharacteristicOrControlChangingContinuousEffect
import mtg.game.ObjectId
import mtg.text.VerbPhraseTemplate

trait CharacteristicOrControlChangingContinuousEffectDescription {
  def getVerbPhraseTemplate(cardName: String): VerbPhraseTemplate
  def getEffect(objectId: ObjectId): CharacteristicOrControlChangingContinuousEffect
}
