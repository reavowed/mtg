package mtg.data.cards.warofthespark

import mtg.abilities.keyword.Hexproof
import mtg.cards.patterns.Creature
import mtg.characteristics.Color.Green
import mtg.characteristics.types.CreatureType.Crocodile
import mtg.parts.costs.ManaCost

object WardscaleCrocodile extends Creature(
  "Wardscale Crocodile",
  ManaCost(4, Green),
  Seq(Crocodile),
  Hexproof,
  (5, 3))
