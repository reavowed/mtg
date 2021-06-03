package mtg.data.cards.m21

import mtg.abilities.keyword.Flying
import mtg.cards.patterns.Creature
import mtg.characteristics.Color.White
import mtg.characteristics.types.CreatureType.Pegasus
import mtg.parts.costs.ManaCost

object ConcordiaPegasus extends Creature(
  "Concordia Pegasus",
  ManaCost(1, White),
  Seq(Pegasus),
  Flying,
  (1, 3))
