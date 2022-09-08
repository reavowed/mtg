package mtg.sets.strixhaven.cards

import mtg.cards.patterns.VanillaCreatureCard
import mtg.definitions.symbols.ManaSymbol.Green
import mtg.definitions.types.CreatureType.Crocodile
import mtg.parts.costs.ManaCost

object SpinedKarok extends VanillaCreatureCard(
  "Spined Karok",
  ManaCost(2, Green),
  Seq(Crocodile),
  (2, 4))
