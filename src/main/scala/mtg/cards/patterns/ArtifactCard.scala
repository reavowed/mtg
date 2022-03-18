package mtg.cards.patterns

import mtg.cards.CardDefinition
import mtg.cards.text.TextParagraph
import mtg.core.types.Type
import mtg.parts.costs.ManaCost

class ArtifactCard(
    name: String,
    manaCost: ManaCost,
    textParagraphs: Seq[TextParagraph])
  extends CardDefinition(
    name,
    Some(manaCost),
    None,
    Nil,
    Seq(Type.Creature),
    Nil,
    textParagraphs,
    None,
    None)
