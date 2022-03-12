package mtg.data.cards.strixhaven

import mtg.abilities.builder.EffectBuilder._
import mtg.abilities.keyword.Vigilance
import mtg.cards.patterns.Spell
import mtg.cards.text.SimpleInstructionParagraph
import mtg.core.types.SpellType.Lesson
import mtg.core.types.Type
import mtg.core.types.Type.Creature
import mtg.parts.costs.ManaCost
import mtg.parts.counters.PlusOnePlusOneCounter

object ExpandedAnatomy extends Spell(
  "Expanded Anatomy",
  ManaCost(3),
  Type.Sorcery,
  Seq(Lesson),
  SimpleInstructionParagraph(
    put(2, PlusOnePlusOneCounter).on(target(Creature)),
    it(gains(Vigilance)).until(endOfTurn))
)
