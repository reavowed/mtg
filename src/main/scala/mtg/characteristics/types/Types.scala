package mtg.characteristics.types

import mtg.text.NounPhraseTemplate
import mtg.utils.CaseObjectWithName

sealed class Supertype extends CaseObjectWithName

object Supertype {
  object Basic extends Supertype
}

sealed trait Type extends CaseObjectWithName {
  def nounPhraseTemplate: NounPhraseTemplate = NounPhraseTemplate.Simple(name.toLowerCase)
}

object Type {
  trait InstantOrSorcery extends Type

  case object Artifact extends Type
  case object Creature extends Type
  case object Enchantment extends Type
  case object Instant extends InstantOrSorcery
  case object Land extends Type
  case object Planeswalker extends Type
  case object Sorcery extends InstantOrSorcery {
    override def nounPhraseTemplate: NounPhraseTemplate = NounPhraseTemplate.Simple("sorcery", "sorceries")
  }
  // Unsupported (so far): Conspiracy, Dungeon, Phenomenon, Plane, Scheme, Tribal, Vanguard
}

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
