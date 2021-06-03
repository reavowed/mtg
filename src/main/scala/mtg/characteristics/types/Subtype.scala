package mtg.characteristics.types

import mtg.abilities.ActivatedAbilityDefinition
import mtg.characteristics.Color
import mtg.effects.oneshot.basic
import mtg.parts.costs.TapSymbol
import mtg.utils.CaseObjectWithName

sealed class Subtype extends CaseObjectWithName

sealed class LandType extends Subtype

sealed class BasicLandType(val color: Color) extends LandType {
  def intrinsicManaAbility: ActivatedAbilityDefinition = ActivatedAbilityDefinition(Seq(TapSymbol), basic.AddManaEffect(color))
}
object BasicLandType {
  object Plains extends BasicLandType(Color.White)
  object Island extends BasicLandType(Color.Blue)
  object Swamp extends BasicLandType(Color.Black)
  object Mountain extends BasicLandType(Color.Red)
  object Forest extends BasicLandType(Color.Green)
}

sealed class CreatureType extends Subtype
object CreatureType {
  object Cat extends CreatureType
  object Dog extends CreatureType
  object Crocodile extends CreatureType
  object Elemental extends CreatureType
  object Elf extends CreatureType
  object Pegasus extends CreatureType
  object Soldier extends CreatureType
  object Spider extends CreatureType
  object Spirit extends CreatureType
  object Warrior extends CreatureType
}

sealed class SpellType extends Subtype
object SpellType {
  object Lesson extends SpellType
}
