package mtg.data.sets.alpha.cards

import mtg.cards.patterns.VanillaCreatureCard
import mtg.core.symbols.ManaSymbol.White
import mtg.core.types.CreatureType.Cat
import mtg.parts.costs.ManaCost

object SavannahLions extends VanillaCreatureCard(
  "Savannah Lions",
  ManaCost(White),
  Seq(Cat),
  (2, 1))
