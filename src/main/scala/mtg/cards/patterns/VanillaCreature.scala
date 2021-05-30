package mtg.cards.patterns

import mtg.characteristics.types.CreatureType
import mtg.parts.costs.ManaCost

class VanillaCreature(
    name: String,
    manaCost: ManaCost,
    subtypes: Seq[CreatureType],
    powerAndToughness: (Int, Int))
  extends Creature(
    name,
    manaCost,
    subtypes,
    Nil,
    powerAndToughness)
