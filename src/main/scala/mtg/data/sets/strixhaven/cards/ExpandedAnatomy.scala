package mtg.data.sets.strixhaven.cards

import mtg.abilities.builder.InstructionBuilder._
import mtg.abilities.builder.TypeConversions._
import mtg.abilities.keyword.Vigilance
import mtg.cards.patterns.SpellCard
import mtg.cards.text.SimpleInstructionParagraph
import mtg.core.types.SpellType.Lesson
import mtg.core.types.Type
import mtg.core.types.Type.Creature
import mtg.instructions.nounPhrases.{It, Target}
import mtg.instructions.verbs.{Gain, PutCounters}
import mtg.parts.costs.ManaCost
import mtg.parts.counters.PlusOnePlusOneCounter

object ExpandedAnatomy extends SpellCard(
  "Expanded Anatomy",
  ManaCost(3),
  Type.Sorcery,
  Seq(Lesson),
  SimpleInstructionParagraph(
    PutCounters(2, PlusOnePlusOneCounter)(Target(Creature)),
    It(Gain(Vigilance), endOfTurn)))
