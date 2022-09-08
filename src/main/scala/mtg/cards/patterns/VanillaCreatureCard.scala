package mtg.cards.patterns

import mtg.definitions.types.CreatureType
import mtg.parts.costs.ManaCost

class VanillaCreatureCard(
    name: String,
    manaCost: ManaCost,
    subtypes: Seq[CreatureType],
    powerAndToughness: (Int, Int))
  extends CreatureCard(
    name,
    manaCost,
    subtypes,
    Nil,
    powerAndToughness)
