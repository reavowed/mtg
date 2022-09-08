package mtg.sets.coreSet2021.cards

import mtg.abilities.keyword.Vigilance
import mtg.cards.patterns.CreatureCard
import mtg.definitions.symbols.ManaSymbol.White
import mtg.definitions.types.CreatureType.Dog
import mtg.parts.costs.ManaCost

case object AlpineWatchdog extends CreatureCard(
  "Alpine Watchdog",
  ManaCost(1, White),
  Seq(Dog),
  Vigilance,
  (2, 2))
