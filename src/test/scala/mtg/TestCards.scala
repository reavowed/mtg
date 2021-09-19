package mtg

import mtg.cards.patterns.VanillaCreature
import mtg.parts.costs.ManaCost

object TestCards {
  object VanillaOneOne extends VanillaCreature(
    "Test 1/1",
    ManaCost(0),
    Nil,
    (1, 1))
  object VanillaTwoTwo extends VanillaCreature(
    "Test 2/2",
    ManaCost(0),
    Nil,
    (2, 2))

}
