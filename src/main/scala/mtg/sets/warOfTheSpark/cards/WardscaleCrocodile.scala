package mtg.sets.warOfTheSpark.cards

import mtg.abilities.keyword.Hexproof
import mtg.cards.patterns.CreatureCard
import mtg.definitions.symbols.ManaSymbol.Green
import mtg.definitions.types.CreatureType.Crocodile
import mtg.parts.costs.ManaCost

object WardscaleCrocodile extends CreatureCard(
  "Wardscale Crocodile",
  ManaCost(4, Green),
  Seq(Crocodile),
  Hexproof,
  (5, 3))
