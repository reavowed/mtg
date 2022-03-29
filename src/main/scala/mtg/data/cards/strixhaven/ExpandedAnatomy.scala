package mtg.data.cards.strixhaven

import mtg.abilities.builder.InstructionBuilder._
import mtg.abilities.keyword.Vigilance
import mtg.cards.patterns.SpellCard
import mtg.cards.text.SimpleInstructionParagraph
import mtg.core.types.SpellType.Lesson
import mtg.core.types.Type
import mtg.core.types.Type.Creature
import mtg.instructions.verbs.PutCounters
import mtg.instructions.nouns.It
import mtg.parts.costs.ManaCost
import mtg.parts.counters.PlusOnePlusOneCounter

object ExpandedAnatomy extends SpellCard(
  "Expanded Anatomy",
  ManaCost(3),
  Type.Sorcery,
  Seq(Lesson),
  SimpleInstructionParagraph(
    PutCounters(2, PlusOnePlusOneCounter)(target(Creature)),
    It(gains(Vigilance)).until(endOfTurn))
)
