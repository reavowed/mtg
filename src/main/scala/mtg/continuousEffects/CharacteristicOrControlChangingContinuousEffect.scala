package mtg.continuousEffects

import mtg.definitions.ObjectId

trait CharacteristicOrControlChangingContinuousEffect extends ContinuousEffect {
  def affectedObject: ObjectId
}
