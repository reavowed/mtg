package mtg.data.sets.warOfTheSpark.cards

import mtg.abilities.keyword.Hexproof
import mtg.cards.patterns.CreatureCard
import mtg.core.symbols.ManaSymbol.Green
import mtg.core.types.CreatureType.Crocodile
import mtg.parts.costs.ManaCost

object WardscaleCrocodile extends CreatureCard(
  "Wardscale Crocodile",
  ManaCost(4, Green),
  Seq(Crocodile),
  Hexproof,
  (5, 3))
