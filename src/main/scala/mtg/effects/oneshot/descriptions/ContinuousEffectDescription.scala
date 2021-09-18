package mtg.effects.oneshot.descriptions

import mtg.effects.ContinuousEffect
import mtg.game.ObjectId
import mtg.text.VerbPhraseTemplate

trait ContinuousEffectDescription {
  def getVerbPhraseTemplate(cardName: String): VerbPhraseTemplate
  def getEffect(objectId: ObjectId): ContinuousEffect
}
