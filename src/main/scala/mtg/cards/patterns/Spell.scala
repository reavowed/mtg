package mtg.cards.patterns

import mtg.abilities.AbilityDefinition
import mtg.cards.CardDefinition
import mtg.characteristics.types.{SpellType, Type}
import mtg.parts.costs.ManaCost

class Spell(
    name: String,
    manaCost: ManaCost,
    `type`: Type.InstantOrSorcery,
    subtypes: Seq[SpellType],
    abilities: Seq[AbilityDefinition])
  extends CardDefinition(
    name,
    Some(manaCost),
    None,
    Nil,
    Seq(`type`),
    subtypes,
    abilities,
    None,
    None)
