package mtg.sets.kaldheim.cards

import mtg.abilities.keyword.Reach
import mtg.cards.patterns.CreatureCard
import mtg.definitions.symbols.ManaSymbol.Green
import mtg.definitions.types.CreatureType.Spider
import mtg.parts.costs.ManaCost

object GnottvoldRecluse extends CreatureCard(
  "Gnottvold Recluse",
  ManaCost(2, Green),
  Seq(Spider),
  Reach,
  (4, 2))
