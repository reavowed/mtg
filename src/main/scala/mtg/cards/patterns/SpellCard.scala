package mtg.cards.patterns

import mtg.cards.CardDefinition
import mtg.cards.text.TextParagraph
import mtg.definitions.types.{SpellType, Type}
import mtg.parts.costs.ManaCost

class SpellCard(
    name: String,
    manaCost: ManaCost,
    `type`: Type.InstantOrSorcery,
    subtypes: Seq[SpellType],
    textParagraphs: Seq[TextParagraph])
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
{
  def this(name: String, manaCost: ManaCost, `type`: Type.InstantOrSorcery, textParagraphs: Seq[TextParagraph]) = {
    this(name, manaCost, `type`, Nil, textParagraphs)
  }
}
