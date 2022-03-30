package mtg.data.sets.strixhaven.cards

import mtg.abilities.builder.InstructionBuilder._
import mtg.abilities.builder.TypeConversions._
import mtg.cards.patterns.CreatureCard
import mtg.cards.text.AbilityWordParagraph
import mtg.core.symbols.ManaSymbol.White
import mtg.core.types.CreatureType.{Human, Wizard}
import mtg.core.types.Type.{Instant, Sorcery}
import mtg.instructions.articles.A
import mtg.instructions.conditions.Whenever
import mtg.instructions.joiners.Or
import mtg.instructions.nounPhrases.{CardName, You}
import mtg.instructions.nouns.Spell
import mtg.instructions.verbs.{Cast, Copy, Get}
import mtg.parts.costs.ManaCost

object EagerFirstYear extends CreatureCard(
  "Eager First-Year",
  ManaCost(1, White),
  Seq(Human, Wizard),
  AbilityWordParagraph(
    "Magecraft",
    Whenever(You, Or(Cast, Copy), A(Or(Instant, Sorcery)(Spell)))(CardName(Get(1, 0), endOfTurn))),
  (2, 2))
