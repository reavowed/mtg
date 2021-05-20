package mtg.cards.patterns

import mtg.cards.{CardDefinition, PowerAndToughness}
import mtg.characteristics.types.{CreatureType, Type}
import mtg.parts.costs.ManaCost

class VanillaCreature(
    name: String,
    manaCost: ManaCost,
    subtypes: Seq[CreatureType],
    powerAndToughness: (Int, Int))
  extends CardDefinition(
    name,
    Some(manaCost),
    None,
    Nil,
    Seq(Type.Creature),
    subtypes,
    Nil,
    Some(PowerAndToughness.Fixed(powerAndToughness._1, powerAndToughness._2)),
    None)
