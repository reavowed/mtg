package mtg.characteristics.types

import mtg.abilities.ActivatedAbilityDefinition
import mtg.core.symbols.ManaSymbol
import mtg.effects.oneshot.basic
import mtg.parts.costs.TapSymbol
import mtg.text.NounPhraseTemplate
import mtg.utils.CaseObjectWithName

sealed class Supertype extends CaseObjectWithName

object Supertype {
  object Basic extends Supertype
}

sealed trait Type extends CaseObjectWithName {
  def isSpell: Boolean
  def isPermanent: Boolean
  def nounPhraseTemplate: NounPhraseTemplate = NounPhraseTemplate.Simple(name.toLowerCase)
}

object Type {
  trait InstantOrSorcery extends Type {
    override def isSpell: Boolean = true
    override def isPermanent: Boolean = false
  }

  case object Land extends Type {
    override def isSpell: Boolean = false
    override def isPermanent: Boolean = true
  }
  case object Instant extends InstantOrSorcery
  case object Sorcery extends InstantOrSorcery {
    override def nounPhraseTemplate: NounPhraseTemplate = NounPhraseTemplate.Simple("sorcery", "sorceries")
  }
  case object Creature extends Type {
    override def isSpell: Boolean = true
    override def isPermanent: Boolean = true
  }
  case object Planeswalker extends Type {
    override def isSpell: Boolean = true
    override def isPermanent: Boolean = true
  }
}

sealed class Subtype extends CaseObjectWithName

sealed class LandType extends Subtype

sealed class BasicLandType(val manaSymbol: ManaSymbol) extends LandType {
  def intrinsicManaAbility: ActivatedAbilityDefinition = ActivatedAbilityDefinition(Seq(TapSymbol), basic.AddManaEffect(manaSymbol))
}
object BasicLandType {
  object Plains extends BasicLandType(ManaSymbol.White)
  object Island extends BasicLandType(ManaSymbol.Blue)
  object Swamp extends BasicLandType(ManaSymbol.Black)
  object Mountain extends BasicLandType(ManaSymbol.Red)
  object Forest extends BasicLandType(ManaSymbol.Green)
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
