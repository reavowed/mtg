package mtg.data.cards.alpha

import mtg.cards.patterns.VanillaCreatureCard
import mtg.core.symbols.ManaSymbol.White
import mtg.core.types.CreatureType._
import mtg.parts.costs.ManaCost

object SavannahLions extends VanillaCreatureCard(
  "Savannah Lions",
  ManaCost(White),
  Seq(Cat),
  (2, 1))
