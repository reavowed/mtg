package mtg.cards

import mtg.abilities.Ability
import mtg.characteristics.types.{Subtype, Supertype, Type}
import mtg.parts.costs.ManaCost

class CardDefinition(
  val name: String,
  val manaCost: Option[ManaCost],
  val colorIndicator: Option[ColorIndicator],
  val superTypes: Seq[Supertype],
  val types: Seq[Type],
  val subTypes: Seq[Subtype],
  val oracleText: String,
  val abilitiesFromRulesText: Seq[Ability],
  val powerAndToughness: Option[PowerAndToughness],
  val loyalty: Option[Int])
{
  override def toString: String = getClass.getSimpleName
}
