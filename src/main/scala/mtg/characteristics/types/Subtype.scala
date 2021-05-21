package mtg.characteristics.types

import mtg.abilities.ActivatedAbilityDefinition
import mtg.characteristics.Color
import mtg.effects.AddManaEffect
import mtg.parts.costs.TapSymbol

sealed class Subtype

sealed class LandType extends Subtype
sealed class CreatureType(val name: String) extends Subtype

sealed class BasicLandType(val name: String, val color: Color) extends LandType {
  def intrinsicManaAbility: ActivatedAbilityDefinition = ActivatedAbilityDefinition(Seq(TapSymbol), Seq(AddManaEffect(color)))
}

object BasicLandType {
  val Plains = new BasicLandType("Plains", Color.White)
  val Island = new BasicLandType("Island", Color.Blue)
  val Swamp = new BasicLandType("Swamp", Color.Black)
  val Mountain = new BasicLandType("Mountain", Color.Red)
  val Forest = new BasicLandType("Forest", Color.Green)
}

object CreatureType {
  val Soldier = new CreatureType("Soldier")
  val Spirit = new CreatureType("Spirit")
}
