package mtg.data.sets.strixhaven.cards

import mtg.abilities.builder.TypeConversions._
import mtg.cards.patterns.SpellCard
import mtg.cards.text.{InstructionSentence, SimpleInstructionParagraph}
import mtg.core.types.SpellType.Lesson
import mtg.core.types.Supertype.Basic
import mtg.core.types.Type
import mtg.core.types.Type.Land
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
