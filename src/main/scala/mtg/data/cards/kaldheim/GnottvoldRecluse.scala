package mtg.data.cards.kaldheim

import mtg.abilities.keyword.Reach
import mtg.cards.patterns.CreatureCard
import mtg.core.symbols.ManaSymbol.Green
import mtg.core.types.CreatureType.Spider
import mtg.parts.costs.ManaCost

object GnottvoldRecluse extends CreatureCard(
  "Gnottvold Recluse",
  ManaCost(2, Green),
  Seq(Spider),
  Reach,
  (4, 2))
