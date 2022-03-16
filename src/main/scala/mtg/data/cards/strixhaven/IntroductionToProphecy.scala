package mtg.data.cards.strixhaven

import mtg.abilities.builder.InstructionBuilder._
import mtg.cards.patterns.Spell
import mtg.cards.text.InstructionSentence
import mtg.core.types.SpellType.Lesson
import mtg.core.types.Type
import mtg.instructions.actions.DrawACard
import mtg.parts.costs.ManaCost

object IntroductionToProphecy extends Spell(
  "Introduction to Prophecy",
  ManaCost(3),
  Type.Sorcery,
  Seq(Lesson),
  InstructionSentence.MultiClause(scry(2), DrawACard))
