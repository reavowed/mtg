package mtg.sets.strixhaven.cards

import mtg.abilities.keyword.Flying
import mtg.cards.patterns.CreatureCard
import mtg.definitions.symbols.ManaSymbol.White
import mtg.definitions.types.CreatureType._
import mtg.parts.costs.ManaCost

object StoneriseSpirit extends CreatureCard(
  "Stonerise Spirit",
  ManaCost(1, White),
  Seq(Spirit, Bird),
  Seq(
    Flying,
    ),
  (0, 0))
