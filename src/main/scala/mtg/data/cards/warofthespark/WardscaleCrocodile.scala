package mtg.data.cards.warofthespark

import mtg.abilities.keyword.Hexproof
import mtg.cards.patterns.Creature
import mtg.core.symbols.ManaSymbol.Green
import mtg.core.types.CreatureType.Crocodile
import mtg.parts.costs.ManaCost

object WardscaleCrocodile extends Creature(
  "Wardscale Crocodile",
  ManaCost(4, Green),
  Seq(Crocodile),
  Hexproof,
  (5, 3))
