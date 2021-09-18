package mtg.data.cards.alpha

import mtg.abilities.keyword.Flying
import mtg.cards.patterns.Creature
import mtg.characteristics.Color.Blue
import mtg.characteristics.types.CreatureType.Elemental
import mtg.parts.costs.ManaCost

object AirElemental extends Creature(
  "Concordia Pegasus",
  ManaCost(3, Blue, Blue),
  Seq(Elemental),
  Flying,
  (4, 4))