package mtg.sets.strixhaven.cards

import mtg.abilities.builder.TypeConversions._
import mtg.cards.patterns.SpellCard
import mtg.cards.text.{InstructionSentence, SimpleInstructionParagraph}
import mtg.definitions.types.SpellType.Lesson
import mtg.definitions.types.Supertype.Basic
import mtg.definitions.types.Type
import mtg.definitions.types.Type.Land
import mtg.instructions.nounPhrases
import mtg.instructions.nounPhrases.It
import mtg.instructions.nouns.Card
import mtg.instructions.verbs._
import mtg.parts.costs.ManaCost

object EnvironmentalSciences extends SpellCard(
  "Environmental Sciences",
  ManaCost(2),
  Type.Sorcery,
  Seq(Lesson),
  SimpleInstructionParagraph(
    InstructionSentence.MultiClause(SearchYourLibraryFor(Basic(Land(Card))), Reveal(It), PutIntoYourHand(It), Shuffle),
    nounPhrases.You(GainLife(2))))
