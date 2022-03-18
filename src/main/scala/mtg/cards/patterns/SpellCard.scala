package mtg.cards.patterns

import mtg.cards.CardDefinition
import mtg.cards.text.InstructionParagraph
import mtg.core.types.{SpellType, Type}
import mtg.parts.costs.ManaCost

class SpellCard(
    name: String,
    manaCost: ManaCost,
    `type`: Type.InstantOrSorcery,
    subtypes: Seq[SpellType],
    textParagraphs: Seq[InstructionParagraph])
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
