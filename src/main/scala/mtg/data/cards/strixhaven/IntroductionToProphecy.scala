package mtg.data.cards.strixhaven

import mtg.cards.patterns.SpellCard
import mtg.cards.text.InstructionSentence
import mtg.core.types.SpellType.Lesson
import mtg.core.types.Type
import mtg.instructions.verbs.{DrawACard, Scry}
import mtg.parts.costs.ManaCost

object IntroductionToProphecy extends SpellCard(
  "Introduction to Prophecy",
  ManaCost(3),
  Type.Sorcery,
  Seq(Lesson),
  InstructionSentence.MultiClause(Scry(2), DrawACard))
