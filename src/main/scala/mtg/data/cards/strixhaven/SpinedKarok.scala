package mtg.data.cards.strixhaven

import mtg.cards.patterns.VanillaCreature
import mtg.characteristics.Color.Green
import mtg.characteristics.types.CreatureType._
import mtg.parts.costs.ManaCost

object SpinedKarok extends VanillaCreature(
  "Spined Karok",
  ManaCost(2, Green),
  Seq(Crocodile),
  (2, 4))
