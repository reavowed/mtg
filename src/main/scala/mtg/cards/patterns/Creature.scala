package mtg.cards.patterns

import mtg.cards.text.TextParagraph
import mtg.cards.{CardDefinition, PowerAndToughness}
import mtg.characteristics.types.{CreatureType, Type}
import mtg.parts.costs.ManaCost

class Creature(
    name: String,
    manaCost: ManaCost,
    subtypes: Seq[CreatureType],
    textParagraphs: Seq[TextParagraph],
    powerAndToughness: (Int, Int))
  extends CardDefinition(
    name,
    Some(manaCost),
    None,
    Nil,
    Seq(Type.Creature),
    subtypes,
    textParagraphs,
    Some(PowerAndToughness.Fixed(powerAndToughness._1, powerAndToughness._2)),
    None)
