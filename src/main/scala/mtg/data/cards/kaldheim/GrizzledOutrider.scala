package mtg.data.cards.kaldheim

import mtg.cards.patterns.VanillaCreature
import mtg.characteristics.Color.Green
import mtg.characteristics.types.CreatureType.{Elf, Warrior}
import mtg.parts.costs.ManaCost

object GrizzledOutrider extends VanillaCreature(
  "Grizzled Outrider",
  ManaCost(4, Green),
  Seq(Elf, Warrior),
  (5, 5))
