package mtg.sets.strixhaven.cards

import mtg.abilities.builder.InstructionBuilder._
import mtg.abilities.builder.TypeConversions._
import mtg.abilities.keyword.Vigilance
import mtg.cards.patterns.SpellCard
import mtg.cards.text.SimpleInstructionParagraph
import mtg.core.types.SpellType.Lesson
import mtg.core.types.Type
import mtg.core.types.Type.Creature
import mtg.instructions.nounPhrases.{It, Target}
import mtg.instructions.verbs.{Gain, Put}
import mtg.parts.Counter
import mtg.parts.costs.ManaCost

object ExpandedAnatomy extends SpellCard(
  "Expanded Anatomy",
  ManaCost(3),
  Type.Sorcery,
  Seq(Lesson),
  SimpleInstructionParagraph(
    Put(2, Counter.PlusOnePlusOne)(Target(Creature)),
    It(Gain(Vigilance), endOfTurn)))
