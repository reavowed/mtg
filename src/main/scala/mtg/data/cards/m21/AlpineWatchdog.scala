package mtg.data.cards.m21

import mtg.abilities.keyword.Vigilance
import mtg.cards.patterns.Creature
import mtg.characteristics.types.CreatureType.Dog
import mtg.core.symbols.ManaSymbol.White
import mtg.parts.costs.ManaCost

case object AlpineWatchdog extends Creature(
  "Alpine Watchdog",
  ManaCost(1, White),
  Seq(Dog),
  Vigilance,
  (2, 2))
