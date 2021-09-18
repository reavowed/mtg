package mtg.effects.continuous

import mtg.effects.ContinuousEffect
import mtg.game.ObjectId

trait CharacteristicOrControlChangingContinuousEffect extends ContinuousEffect {
  def affectedObject: ObjectId
}
