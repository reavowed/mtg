package mtg.sets.alpha.cards

import mtg.cards.patterns.VanillaCreatureCard
import mtg.definitions.symbols.ManaSymbol.White
import mtg.definitions.types.CreatureType.Cat
import mtg.parts.costs.ManaCost

object SavannahLions extends VanillaCreatureCard(
  "Savannah Lions",
  ManaCost(White),
  Seq(Cat),
  (2, 1))
