package mtg.effects.oneshot.descriptions

import mtg.effects.ContinuousEffect
import mtg.game.ObjectId

trait ContinuousEffectDescription {
  def getText(cardName: String): String
  def getEffect(objectId: ObjectId): ContinuousEffect
}
