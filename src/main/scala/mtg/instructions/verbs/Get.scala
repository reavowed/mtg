package mtg.instructions.verbs

import mtg.continuousEffects.{CharacteristicOrControlChangingContinuousEffect, ModifyPowerToughnessEffect}
import mtg.core.ObjectId
import mtg.effects.PowerToughnessModifier
import mtg.instructions.{CharacteristicChangingVerb, Verb}

case class Get(powerToughnessModifier: PowerToughnessModifier) extends Verb.WithSuffix(Verb.Get, powerToughnessModifier.description) with CharacteristicChangingVerb {
  override def getEffects(objectId: ObjectId): Seq[CharacteristicOrControlChangingContinuousEffect] = {
    Seq(ModifyPowerToughnessEffect(objectId, powerToughnessModifier))
  }
}

object Get {
  def apply(powerModifier: Int, toughnessModifier: Int): Get = Get(PowerToughnessModifier(powerModifier, toughnessModifier))
}
