package mtg.sets.alpha.cards

import mtg.abilities.keyword.Flying
import mtg.cards.patterns.CreatureCard
import mtg.definitions.symbols.ManaSymbol.Blue
import mtg.definitions.types.CreatureType.Elemental
import mtg.parts.costs.ManaCost

object AirElemental extends CreatureCard(
  "Air Elemental",
  ManaCost(3, Blue, Blue),
  Seq(Elemental),
  Flying,
  (4, 4))
