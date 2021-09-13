package mtg.data.cards.alpha

import mtg.cards.patterns.VanillaCreature
import mtg.characteristics.Color.White
import mtg.characteristics.types.CreatureType._
import mtg.parts.costs.ManaCost

object SavannahLions extends VanillaCreature(
  "Savannah Lions",
  ManaCost(White),
  Seq(Cat),
  (2, 1))
