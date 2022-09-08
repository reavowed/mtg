package mtg.definitions.types

import mtg.utils.CaseObjectWithName

sealed class Subtype extends CaseObjectWithName

sealed class LandType extends Subtype

sealed class BasicLandType extends LandType
object BasicLandType {
  object Plains extends BasicLandType
  object Island extends BasicLandType
  object Swamp extends BasicLandType
  object Mountain extends BasicLandType
  object Forest extends BasicLandType
}

sealed class CreatureType extends Subtype
object CreatureType {
  object Bird extends CreatureType
  object Cat extends CreatureType
  object Cleric extends CreatureType
  object Dog extends CreatureType
  object Crocodile extends CreatureType
  object Elemental extends CreatureType
  object Elf extends CreatureType
  object Human extends CreatureType
  object Pegasus extends CreatureType
  object Soldier extends CreatureType
  object Spider extends CreatureType
  object Spirit extends CreatureType
  object Warrior extends CreatureType
  object Wizard extends CreatureType
}

sealed class SpellType extends Subtype
object SpellType {
  object Lesson extends SpellType
}
