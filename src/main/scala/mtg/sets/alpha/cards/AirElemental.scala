package mtg.sets.alpha.cards

import mtg.abilities.keyword.Flying
import mtg.cards.patterns.CreatureCard
import mtg.core.symbols.ManaSymbol.Blue
import mtg.core.types.CreatureType.Elemental
import mtg.parts.costs.ManaCost

object AirElemental extends CreatureCard(
  "Concordia Pegasus",
  ManaCost(3, Blue, Blue),
  Seq(Elemental),
  Flying,
  (4, 4))
