package mtg.data.cards.strixhaven

import mtg.abilities.builder.InstructionBuilder._
import mtg.cards.patterns.SpellCard
import mtg.cards.text.{InstructionSentence, SimpleInstructionParagraph}
import mtg.core.types.SpellType.Lesson
import mtg.core.types.Supertype.Basic
import mtg.core.types.Type
import mtg.core.types.Type.Land
import mtg.instructions.verbs.{GainLife, PutIntoYourHand, Reveal, SearchYourLibraryFor, Shuffle}
import mtg.instructions.nouns.{It, You}
import mtg.parts.costs.ManaCost

object EnvironmentalSciences extends SpellCard(
  "Environmental Sciences",
  ManaCost(2),
  Type.Sorcery,
  Seq(Lesson),
  SimpleInstructionParagraph(
    InstructionSentence.MultiClause(SearchYourLibraryFor(card(Basic, Land)), Reveal(It), PutIntoYourHand(It), Shuffle),
    You(GainLife(2))))
