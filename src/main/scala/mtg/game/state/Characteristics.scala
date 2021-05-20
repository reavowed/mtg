package mtg.game.state

import mtg.abilities.Ability
import mtg.cards.ColorIndicator
import mtg.characteristics.Color
import mtg.characteristics.types.{Subtype, Supertype, Type}
import mtg.parts.costs.ManaCost

case class Characteristics(
  name: Option[String],
  manaCost: Option[ManaCost],
  color: Set[Color],
  colorIndicator: Option[ColorIndicator],
  superTypes: Seq[Supertype],
  types: Seq[Type],
  subTypes: Seq[Subtype],
  abilities: Seq[Ability],
  power: Option[Int],
  toughness: Option[Int],
  loyalty: Option[Int])
