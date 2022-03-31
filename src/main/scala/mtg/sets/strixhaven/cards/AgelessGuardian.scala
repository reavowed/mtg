package mtg.sets.strixhaven.cards

import mtg.cards.patterns.VanillaCreatureCard
import mtg.core.symbols.ManaSymbol.White
import mtg.core.types.CreatureType.{Soldier, Spirit}
import mtg.parts.costs.ManaCost

object AgelessGuardian extends VanillaCreatureCard(
  "Ageless Guardian",
  ManaCost(1, White),
  Seq(Spirit, Soldier),
  (1, 4))
