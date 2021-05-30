package mtg.cards.patterns

import mtg.cards.CardDefinition
import mtg.cards.text.SpellEffectParagraph
import mtg.characteristics.types.{SpellType, Type}
import mtg.parts.costs.ManaCost

class Spell(
    name: String,
    manaCost: ManaCost,
    `type`: Type.InstantOrSorcery,
    subtypes: Seq[SpellType],
    textParagraphs: Seq[SpellEffectParagraph])
  extends CardDefinition(
    name,
    Some(manaCost),
    None,
    Nil,
    Seq(`type`),
    subtypes,
    textParagraphs,
    None,
    None)
