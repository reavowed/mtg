package mtg.sets.kaldheim.cards

import mtg.cards.patterns.VanillaCreatureCard
import mtg.definitions.symbols.ManaSymbol.Green
import mtg.definitions.types.CreatureType.{Elf, Warrior}
import mtg.parts.costs.ManaCost

object GrizzledOutrider extends VanillaCreatureCard(
  "Grizzled Outrider",
  ManaCost(4, Green),
  Seq(Elf, Warrior),
  (5, 5))
