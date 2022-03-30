package mtg.data.sets.kaldheim.cards

import mtg.cards.patterns.VanillaCreatureCard
import mtg.core.symbols.ManaSymbol.Green
import mtg.core.types.CreatureType.{Elf, Warrior}
import mtg.parts.costs.ManaCost

object GrizzledOutrider extends VanillaCreatureCard(
  "Grizzled Outrider",
  ManaCost(4, Green),
  Seq(Elf, Warrior),
  (5, 5))
