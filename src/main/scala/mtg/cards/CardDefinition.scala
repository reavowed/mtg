package mtg.cards

import mtg.abilities.Ability
import mtg.parts.costs.ManaCost
import mtg.characteristics.types.{Subtype, Supertype, Type}

class CardDefinition(
  name: String,
  manaCost: Option[ManaCost],
  colorIndicator: Option[ColorIndicator],
  superTypes: Seq[Supertype],
  types: Seq[Type],
  subTypes: Seq[Subtype],
  oracleText: String,
  abilitiesFromRulesText: Seq[Ability],
  powerAndToughness: Option[PowerAndToughness],
  loyalty: Option[Int])
{
  override def toString: String = getClass.getSimpleName
}
