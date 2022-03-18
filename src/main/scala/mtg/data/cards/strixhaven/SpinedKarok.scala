package mtg.data.cards.strixhaven

import mtg.cards.patterns.VanillaCreatureCard
import mtg.core.symbols.ManaSymbol.Green
import mtg.core.types.CreatureType._
import mtg.parts.costs.ManaCost

object SpinedKarok extends VanillaCreatureCard(
  "Spined Karok",
  ManaCost(2, Green),
  Seq(Crocodile),
  (2, 4))
