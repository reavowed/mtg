package mtg.effects.continuous

import mtg.core.ObjectId
import mtg.effects.ContinuousEffect

trait CharacteristicOrControlChangingContinuousEffect extends ContinuousEffect {
  def affectedObject: ObjectId
}
