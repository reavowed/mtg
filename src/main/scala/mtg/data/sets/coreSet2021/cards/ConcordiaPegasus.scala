package mtg.data.sets.coreSet2021.cards

import mtg.abilities.keyword.Flying
import mtg.cards.patterns.CreatureCard
import mtg.core.symbols.ManaSymbol.White
import mtg.core.types.CreatureType.Pegasus
import mtg.parts.costs.ManaCost

object ConcordiaPegasus extends CreatureCard(
  "Concordia Pegasus",
  ManaCost(1, White),
  Seq(Pegasus),
  Flying,
  (1, 3))
