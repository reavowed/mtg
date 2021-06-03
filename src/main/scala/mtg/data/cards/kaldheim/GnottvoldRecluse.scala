package mtg.data.cards.kaldheim

import mtg.abilities.keyword.Reach
import mtg.cards.patterns.Creature
import mtg.characteristics.Color.Green
import mtg.characteristics.types.CreatureType.Spider
import mtg.parts.costs.ManaCost

object GnottvoldRecluse extends Creature(
  "Gnottvold Recluse",
  ManaCost(2, Green),
  Seq(Spider),
  Reach,
  (4, 2))
