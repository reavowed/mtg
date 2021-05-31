package mtg.data.cards.strixhaven

import mtg.abilities.builder.EffectBuilder._
import mtg.abilities.keyword.Vigilance
import mtg.cards.patterns.Spell
import mtg.cards.text.SpellEffectParagraph
import mtg.characteristics.types.SpellType.Lesson
import mtg.characteristics.types.Type
import mtg.characteristics.types.Type.Creature
import mtg.parts.costs.ManaCost
import mtg.parts.counters.PlusOnePlusOneCounter

object ExpandedAnatomy extends Spell(
  "Expanded Anatomy",
  ManaCost(3),
  Type.Sorcery,
  Seq(Lesson),
  SpellEffectParagraph(
    put(2, PlusOnePlusOneCounter).on(target(Creature)),
    it.gains(Vigilance).until(endOfTurn))
)
