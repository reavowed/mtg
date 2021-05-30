package mtg.characteristics.types

import mtg.utils.CaseObjectWithName

sealed trait Type extends CaseObjectWithName {
  def isSpell: Boolean
  def isPermanent: Boolean
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
  case object Sorcery extends InstantOrSorcery
  case object Creature extends Type {
    override def isSpell: Boolean = true
    override def isPermanent: Boolean = true
  }
  case object Planeswalker extends Type {
    override def isSpell: Boolean = true
    override def isPermanent: Boolean = true
  }
}
