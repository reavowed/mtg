package mtg.characteristics.types

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import mtg.abilities.ActivatedAbilityDefinition
import mtg.characteristics.Color
import mtg.effects.AddManaEffect
import mtg.parts.costs.TapSymbol
import mtg.utils.CaseObjectSerializer

@JsonSerialize(using = classOf[CaseObjectSerializer])
sealed class Subtype

sealed class LandType extends Subtype

sealed class BasicLandType(val name: String, val color: Color) extends LandType {
  def intrinsicManaAbility: ActivatedAbilityDefinition = ActivatedAbilityDefinition(Seq(TapSymbol), AddManaEffect(color))
}
object BasicLandType {
  object Plains extends BasicLandType("Plains", Color.White)
  object Island extends BasicLandType("Island", Color.Blue)
  object Swamp extends BasicLandType("Swamp", Color.Black)
  object Mountain extends BasicLandType("Mountain", Color.Red)
  object Forest extends BasicLandType("Forest", Color.Green)
}

sealed class CreatureType(val name: String) extends Subtype
object CreatureType {
  object Crocodile extends CreatureType("Crocodile")
  object Elf extends CreatureType("Elf")
  object Soldier extends CreatureType("Soldier")
  object Spirit extends CreatureType("Spirit")
  object Warrior extends CreatureType("Warrior")
}

sealed class SpellType(val name: String) extends Subtype
object SpellType {
  object Lesson extends SpellType("Lesson")
}
