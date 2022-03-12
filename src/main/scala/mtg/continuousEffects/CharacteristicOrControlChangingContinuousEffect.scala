package mtg.continuousEffects

import mtg.core.ObjectId

trait CharacteristicOrControlChangingContinuousEffect extends ContinuousEffect {
  def affectedObject: ObjectId
}
